import React, { useState, useEffect } from 'react'
import { taskService, categoryService } from '../services/api'
import TaskModal from '../components/tasks/TaskModal'
import toast from 'react-hot-toast'
import { Plus, Search, Filter, Trash2, Edit2, Check, ChevronDown } from 'lucide-react'
import { format, isPast, isToday, parseISO } from 'date-fns'

const PRIORITY_COLORS = {
  HIGH: 'priority-high',
  MEDIUM: 'priority-medium',
  LOW: 'priority-low',
}

const STATUS_COLORS = {
  TODO: 'bg-white/10 text-white/60',
  IN_PROGRESS: 'bg-blue-500/15 text-blue-400 border border-blue-500/20',
  COMPLETED: 'bg-emerald-500/15 text-emerald-400 border border-emerald-500/20',
}

export default function TaskManagerPage() {
  const [tasks, setTasks] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editTask, setEditTask] = useState(null)
  const [search, setSearch] = useState('')
  const [filterPriority, setFilterPriority] = useState('')
  const [filterStatus, setFilterStatus] = useState('')
  const [filterCategory, setFilterCategory] = useState('')

  const load = async () => {
    try {
      const [t, c] = await Promise.all([taskService.getAll(), categoryService.getAll()])
      setTasks(t)
      setCategories(c)
    } catch (err) {
      toast.error('Failed to load tasks')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const deleteTask = async (id, e) => {
    e.stopPropagation()
    if (!confirm('Delete this task?')) return
    try {
      await taskService.delete(id)
      setTasks(p => p.filter(t => t.id !== id))
      toast.success('Task deleted')
    } catch { toast.error('Failed to delete') }
  }

  const toggleComplete = async (task, e) => {
    e.stopPropagation()
    const newStatus = task.status === 'COMPLETED' ? 'TODO' : 'COMPLETED'
    try {
      const updated = await taskService.update(task.id, { status: newStatus })
      setTasks(p => p.map(t => t.id === task.id ? updated : t))
    } catch { toast.error('Failed to update') }
  }

  const filtered = tasks.filter(t => {
    const matchSearch = !search || t.title.toLowerCase().includes(search.toLowerCase())
    const matchPriority = !filterPriority || t.priority === filterPriority
    const matchStatus = !filterStatus || t.status === filterStatus
    const matchCat = !filterCategory || t.category?.id == filterCategory
    return matchSearch && matchPriority && matchStatus && matchCat
  })

  if (loading) return (
    <div className="flex items-center justify-center h-full">
      <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
    </div>
  )

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="font-display font-bold text-3xl text-white mb-1">Task Manager</h1>
          <p className="text-white/40">{filtered.length} tasks</p>
        </div>
        <button onClick={() => { setEditTask(null); setShowModal(true) }} className="btn-primary flex items-center gap-2">
          <Plus size={18} />
          New Task
        </button>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3 mb-6">
        <div className="relative flex-1 min-w-[200px]">
          <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-white/30" />
          <input
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search tasks..."
            className="input-field pl-10 py-2.5 text-sm"
          />
        </div>
        <select value={filterPriority} onChange={e => setFilterPriority(e.target.value)}
          className="input-field w-auto py-2.5 text-sm">
          <option value="">All Priorities</option>
          <option value="HIGH">High</option>
          <option value="MEDIUM">Medium</option>
          <option value="LOW">Low</option>
        </select>
        <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}
          className="input-field w-auto py-2.5 text-sm">
          <option value="">All Statuses</option>
          <option value="TODO">To Do</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="COMPLETED">Completed</option>
        </select>
        <select value={filterCategory} onChange={e => setFilterCategory(e.target.value)}
          className="input-field w-auto py-2.5 text-sm">
          <option value="">All Categories</option>
          {categories.map(c => <option key={c.id} value={c.id}>{c.icon} {c.name}</option>)}
        </select>
      </div>

      {/* Task List */}
      {filtered.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 text-center">
          <div className="text-6xl mb-4">📋</div>
          <h3 className="font-display font-semibold text-xl text-white mb-2">No tasks found</h3>
          <p className="text-white/40 mb-6">Create your first task to get started</p>
          <button onClick={() => { setEditTask(null); setShowModal(true) }} className="btn-primary">
            <Plus size={16} className="inline mr-2" />Create Task
          </button>
        </div>
      ) : (
        <div className="space-y-2">
          {filtered.map(task => {
            const isOverdue = task.deadline && isPast(parseISO(task.deadline)) && task.status !== 'COMPLETED'
            const dueToday = task.deadline && isToday(parseISO(task.deadline))
            return (
              <div
                key={task.id}
                onClick={() => { setEditTask(task); setShowModal(true) }}
                className="flex items-center gap-4 p-4 rounded-xl bg-white/[0.03] border border-white/[0.05] hover:border-white/[0.10] hover:bg-white/[0.05] cursor-pointer transition-all duration-200 group animate-fade-in"
              >
                {/* Complete toggle */}
                <button
                  onClick={(e) => toggleComplete(task, e)}
                  className={`w-5 h-5 rounded-md border flex-shrink-0 flex items-center justify-center transition-all
                    ${task.status === 'COMPLETED' ? 'bg-emerald-500 border-emerald-500' : 'border-white/20 hover:border-emerald-500/60'}`}
                >
                  {task.status === 'COMPLETED' && <Check size={12} className="text-white" />}
                </button>

                {/* Title + Meta */}
                <div className="flex-1 min-w-0">
                  <p className={`font-medium ${task.status === 'COMPLETED' ? 'line-through text-white/30' : 'text-white'}`}>
                    {task.title}
                  </p>
                  {task.description && (
                    <p className="text-sm text-white/40 truncate mt-0.5">{task.description}</p>
                  )}
                  {task.subtasks?.length > 0 && (
                    <p className="text-xs text-white/30 mt-1">
                      {task.subtasks.filter(s => s.completed).length}/{task.subtasks.length} subtasks
                    </p>
                  )}
                </div>

                {/* Badges */}
                <div className="flex items-center gap-2 flex-shrink-0">
                  {task.category && (
                    <span className="badge bg-white/[0.06] text-white/60 border border-white/[0.06]">
                      {task.category.icon} {task.category.name}
                    </span>
                  )}
                  <span className={`badge ${PRIORITY_COLORS[task.priority]}`}>{task.priority}</span>
                  <span className={`badge ${STATUS_COLORS[task.status]}`}>
                    {task.status.replace('_', ' ')}
                  </span>
                  {task.deadline && (
                    <span className={`text-xs ${isOverdue ? 'text-red-400' : dueToday ? 'text-amber-400' : 'text-white/40'}`}>
                      {format(parseISO(task.deadline), 'MMM d, HH:mm')}
                    </span>
                  )}
                </div>

                {/* Actions */}
                <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button onClick={(e) => { e.stopPropagation(); setEditTask(task); setShowModal(true) }}
                    className="btn-ghost p-1.5 rounded-lg">
                    <Edit2 size={15} />
                  </button>
                  <button onClick={(e) => deleteTask(task.id, e)}
                    className="p-1.5 rounded-lg text-white/30 hover:text-red-400 hover:bg-red-500/10 transition-all">
                    <Trash2 size={15} />
                  </button>
                </div>
              </div>
            )
          })}
        </div>
      )}

      {showModal && (
        <TaskModal
          task={editTask}
          onClose={() => setShowModal(false)}
          onSave={load}
        />
      )}
    </div>
  )
}
