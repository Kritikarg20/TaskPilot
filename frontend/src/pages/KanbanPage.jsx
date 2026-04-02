import React, { useState, useEffect } from 'react'
import { taskService } from '../services/api'
import TaskModal from '../components/tasks/TaskModal'
import toast from 'react-hot-toast'
import { Plus, GripVertical } from 'lucide-react'
import {
  DndContext, closestCenter, DragOverlay, useSensor, useSensors, PointerSensor
} from '@dnd-kit/core'
import {
  SortableContext, verticalListSortingStrategy, useSortable
} from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'

const COLUMNS = [
  { id: 'TODO', label: 'To Do', color: 'text-white/60', dot: 'bg-white/20' },
  { id: 'IN_PROGRESS', label: 'In Progress', color: 'text-blue-400', dot: 'bg-blue-400' },
  { id: 'COMPLETED', label: 'Completed', color: 'text-emerald-400', dot: 'bg-emerald-400' },
]

const PRIORITY_COLORS = {
  HIGH: 'priority-high',
  MEDIUM: 'priority-medium',
  LOW: 'priority-low',
}

function TaskCard({ task, onClick }) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id: task.id })
  const style = { transform: CSS.Transform.toString(transform), transition, opacity: isDragging ? 0.4 : 1 }

  return (
    <div
      ref={setNodeRef}
      style={style}
      className="bg-[#22222e] border border-white/[0.06] rounded-xl p-4 cursor-pointer hover:border-white/[0.12] transition-all duration-200 group"
      onClick={() => onClick(task)}
    >
      <div className="flex items-start gap-2">
        <div {...attributes} {...listeners}
          className="mt-0.5 text-white/20 hover:text-white/50 cursor-grab active:cursor-grabbing transition-colors flex-shrink-0"
          onClick={e => e.stopPropagation()}>
          <GripVertical size={16} />
        </div>
        <div className="flex-1 min-w-0">
          <p className={`font-medium text-sm ${task.status === 'COMPLETED' ? 'line-through text-white/30' : 'text-white'} mb-2`}>
            {task.title}
          </p>
          {task.description && (
            <p className="text-xs text-white/40 mb-2 line-clamp-2">{task.description}</p>
          )}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className={`badge text-xs ${PRIORITY_COLORS[task.priority]}`}>{task.priority}</span>
              {task.category && (
                <span className="text-xs text-white/40">{task.category.icon}</span>
              )}
            </div>
            {task.subtasks?.length > 0 && (
              <span className="text-xs text-white/30">
                {task.subtasks.filter(s => s.completed).length}/{task.subtasks.length}
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

function KanbanColumn({ column, tasks, onAddTask, onTaskClick }) {
  return (
    <div className="flex flex-col min-w-[300px] max-w-[340px]">
      <div className="flex items-center gap-2.5 mb-4 px-1">
        <div className={`w-2 h-2 rounded-full ${column.dot}`} />
        <h3 className={`font-display font-semibold ${column.color}`}>{column.label}</h3>
        <span className="ml-auto text-xs text-white/30 bg-white/[0.05] px-2 py-0.5 rounded-full">
          {tasks.length}
        </span>
      </div>
      <div className="flex-1 min-h-[400px] rounded-2xl bg-white/[0.02] border border-white/[0.04] p-3 space-y-3">
        <SortableContext items={tasks.map(t => t.id)} strategy={verticalListSortingStrategy}>
          {tasks.map(task => (
            <TaskCard key={task.id} task={task} onClick={onTaskClick} />
          ))}
        </SortableContext>
        <button
          onClick={() => onAddTask(column.id)}
          className="w-full py-2.5 rounded-xl border border-dashed border-white/[0.08] text-white/30 hover:text-white/60 hover:border-white/[0.15] text-sm flex items-center justify-center gap-2 transition-all"
        >
          <Plus size={15} /> Add task
        </button>
      </div>
    </div>
  )
}

export default function KanbanPage() {
  const [tasks, setTasks] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editTask, setEditTask] = useState(null)
  const [defaultStatus, setDefaultStatus] = useState('TODO')
  const [activeTask, setActiveTask] = useState(null)

  const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 8 } }))

  const load = async () => {
    try {
      const t = await taskService.getAll()
      setTasks(t)
    } catch { toast.error('Failed to load') }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  const getColumnTasks = (col) => tasks.filter(t => (t.kanbanColumn || t.status) === col)

  const handleDragStart = (event) => {
    const task = tasks.find(t => t.id === event.active.id)
    setActiveTask(task)
  }

  const handleDragEnd = async (event) => {
    setActiveTask(null)
    const { active, over } = event
    if (!over) return

    const draggedTask = tasks.find(t => t.id === active.id)
    if (!draggedTask) return

    // Find which column the task was dropped into
    let targetColumn = null
    for (const col of COLUMNS) {
      const colTasks = getColumnTasks(col.id)
      if (colTasks.some(t => t.id === over.id) || over.id === col.id) {
        targetColumn = col.id
        break
      }
    }

    if (!targetColumn || targetColumn === (draggedTask.kanbanColumn || draggedTask.status)) return

    try {
      const updated = await taskService.moveKanban(draggedTask.id, { kanbanColumn: targetColumn })
      setTasks(p => p.map(t => t.id === draggedTask.id ? updated : t))
      toast.success(`Moved to ${targetColumn.replace('_', ' ')}`)
    } catch { toast.error('Failed to move task') }
  }

  const openAddTask = (status) => {
    setDefaultStatus(status)
    setEditTask(null)
    setShowModal(true)
  }

  if (loading) return (
    <div className="flex items-center justify-center h-full">
      <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
    </div>
  )

  return (
    <div className="p-6 lg:p-8 h-full">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="font-display font-bold text-3xl text-white mb-1">Kanban Board</h1>
          <p className="text-white/40">Drag and drop tasks between columns</p>
        </div>
        <button onClick={() => openAddTask('TODO')} className="btn-primary flex items-center gap-2">
          <Plus size={18} /> New Task
        </button>
      </div>

      <DndContext sensors={sensors} collisionDetection={closestCenter} onDragStart={handleDragStart} onDragEnd={handleDragEnd}>
        <div className="flex gap-5 overflow-x-auto pb-4">
          {COLUMNS.map(col => (
            <KanbanColumn
              key={col.id}
              column={col}
              tasks={getColumnTasks(col.id)}
              onAddTask={openAddTask}
              onTaskClick={(task) => { setEditTask(task); setShowModal(true) }}
            />
          ))}
        </div>
        <DragOverlay>
          {activeTask && (
            <div className="bg-[#22222e] border border-brand-500/40 rounded-xl p-4 shadow-2xl shadow-brand-500/20 rotate-1">
              <p className="font-medium text-sm text-white">{activeTask.title}</p>
            </div>
          )}
        </DragOverlay>
      </DndContext>

      {showModal && (
        <TaskModal
          task={editTask || { status: defaultStatus }}
          onClose={() => setShowModal(false)}
          onSave={load}
        />
      )}
    </div>
  )
}
