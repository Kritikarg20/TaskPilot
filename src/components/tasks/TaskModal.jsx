import React, { useState, useEffect } from 'react'
import { taskService, categoryService, subtaskService, fileService } from '../../services/api'
import toast from 'react-hot-toast'
import { X, Plus, Trash2, Check, Paperclip, Upload } from 'lucide-react'
import { format } from 'date-fns'

export default function TaskModal({ task, onClose, onSave }) {
  const [categories, setCategories] = useState([])
  const [form, setForm] = useState({
    title: task?.title || '',
    description: task?.description || '',
    priority: task?.priority || 'MEDIUM',
    deadline: task?.deadline ? format(new Date(task.deadline), "yyyy-MM-dd'T'HH:mm") : '',
    status: task?.status || 'TODO',
    categoryId: task?.category?.id || '',
  })
  const [subtasks, setSubtasks] = useState(task?.subtasks || [])
  const [newSubtask, setNewSubtask] = useState('')
  const [loading, setLoading] = useState(false)
  const [files, setFiles] = useState(task?.attachments || [])

  useEffect(() => {
    categoryService.getAll().then(setCategories).catch(console.error)
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const payload = { ...form, categoryId: form.categoryId || null }
      if (payload.deadline === '') payload.deadline = null
      if (task?.id) {
        await taskService.update(task.id, payload)
        toast.success('Task updated')
      } else {
        await taskService.create(payload)
        toast.success('Task created')
      }
      onSave()
      onClose()
    } catch (err) {
      toast.error('Failed to save task')
    } finally {
      setLoading(false)
    }
  }

  const addSubtask = async () => {
    if (!newSubtask.trim() || !task?.id) return
    try {
      const sub = await subtaskService.create({ title: newSubtask, taskId: task.id })
      setSubtasks(p => [...p, sub])
      setNewSubtask('')
    } catch { toast.error('Failed to add subtask') }
  }

  const toggleSubtask = async (sub) => {
    try {
      const updated = await subtaskService.update(sub.id, { completed: !sub.completed })
      setSubtasks(p => p.map(s => s.id === sub.id ? updated : s))
    } catch { toast.error('Failed to update subtask') }
  }

  const deleteSubtask = async (id) => {
    try {
      await subtaskService.delete(id)
      setSubtasks(p => p.filter(s => s.id !== id))
    } catch { toast.error('Failed to delete subtask') }
  }

  const handleFileUpload = async (e) => {
    if (!task?.id) { toast.error('Save task first to attach files'); return }
    const file = e.target.files[0]
    if (!file) return
    const formData = new FormData()
    formData.append('file', file)
    try {
      const res = await fileService.upload(task.id, formData)
      setFiles(p => [...p, res])
      toast.success('File uploaded')
    } catch { toast.error('Upload failed') }
  }

  const deleteFile = async (id) => {
    try {
      await fileService.delete(id)
      setFiles(p => p.filter(f => f.id !== id))
    } catch { toast.error('Delete failed') }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fade-in">
      <div className="bg-[#1a1a23] border border-white/[0.08] rounded-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto animate-slide-up">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-white/[0.06]">
          <h2 className="font-display font-bold text-xl text-white">
            {task?.id ? 'Edit Task' : 'New Task'}
          </h2>
          <button onClick={onClose} className="btn-ghost p-1.5 rounded-lg">
            <X size={18} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="p-6 space-y-5">
            {/* Title */}
            <div>
              <label className="text-xs font-medium text-white/50 mb-1.5 block">Title *</label>
              <input
                type="text"
                value={form.title}
                onChange={e => setForm(p => ({ ...p, title: e.target.value }))}
                className="input-field"
                placeholder="Task title"
                required
              />
            </div>

            {/* Description */}
            <div>
              <label className="text-xs font-medium text-white/50 mb-1.5 block">Description</label>
              <textarea
                value={form.description}
                onChange={e => setForm(p => ({ ...p, description: e.target.value }))}
                className="input-field min-h-[80px] resize-none"
                placeholder="Add details..."
              />
            </div>

            {/* Row: Priority + Status */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-medium text-white/50 mb-1.5 block">Priority</label>
                <select
                  value={form.priority}
                  onChange={e => setForm(p => ({ ...p, priority: e.target.value }))}
                  className="input-field"
                >
                  <option value="HIGH">🔴 High</option>
                  <option value="MEDIUM">🟡 Medium</option>
                  <option value="LOW">🟢 Low</option>
                </select>
              </div>
              <div>
                <label className="text-xs font-medium text-white/50 mb-1.5 block">Status</label>
                <select
                  value={form.status}
                  onChange={e => setForm(p => ({ ...p, status: e.target.value }))}
                  className="input-field"
                >
                  <option value="TODO">To Do</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="COMPLETED">Completed</option>
                </select>
              </div>
            </div>

            {/* Row: Deadline + Category */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-medium text-white/50 mb-1.5 block">Deadline</label>
                <input
                  type="datetime-local"
                  value={form.deadline}
                  onChange={e => setForm(p => ({ ...p, deadline: e.target.value }))}
                  className="input-field"
                />
              </div>
              <div>
                <label className="text-xs font-medium text-white/50 mb-1.5 block">Category</label>
                <select
                  value={form.categoryId}
                  onChange={e => setForm(p => ({ ...p, categoryId: e.target.value }))}
                  className="input-field"
                >
                  <option value="">No category</option>
                  {categories.map(c => (
                    <option key={c.id} value={c.id}>{c.icon} {c.name}</option>
                  ))}
                </select>
              </div>
            </div>

            {/* Subtasks (only for existing tasks) */}
            {task?.id && (
              <div>
                <label className="text-xs font-medium text-white/50 mb-2 block">Subtasks</label>
                <div className="space-y-2 mb-2">
                  {subtasks.map(sub => (
                    <div key={sub.id} className="flex items-center gap-3 py-2 px-3 rounded-xl bg-white/[0.03] border border-white/[0.04] group">
                      <button type="button" onClick={() => toggleSubtask(sub)}
                        className={`w-5 h-5 rounded-md border flex items-center justify-center flex-shrink-0 transition-colors
                          ${sub.completed ? 'bg-emerald-500 border-emerald-500' : 'border-white/20 hover:border-emerald-500/50'}`}>
                        {sub.completed && <Check size={12} className="text-white" />}
                      </button>
                      <span className={`text-sm flex-1 ${sub.completed ? 'line-through text-white/30' : 'text-white/80'}`}>
                        {sub.title}
                      </span>
                      <button type="button" onClick={() => deleteSubtask(sub.id)}
                        className="text-white/20 hover:text-red-400 opacity-0 group-hover:opacity-100 transition-all">
                        <Trash2 size={14} />
                      </button>
                    </div>
                  ))}
                </div>
                <div className="flex gap-2">
                  <input
                    type="text"
                    value={newSubtask}
                    onChange={e => setNewSubtask(e.target.value)}
                    onKeyDown={e => e.key === 'Enter' && (e.preventDefault(), addSubtask())}
                    placeholder="Add subtask..."
                    className="input-field text-sm py-2"
                  />
                  <button type="button" onClick={addSubtask} className="btn-ghost px-3 py-2">
                    <Plus size={16} />
                  </button>
                </div>
              </div>
            )}

            {/* Files */}
            {task?.id && (
              <div>
                <label className="text-xs font-medium text-white/50 mb-2 block">Attachments</label>
                <div className="space-y-2 mb-2">
                  {files.map(f => (
                    <div key={f.id} className="flex items-center gap-3 py-2 px-3 rounded-xl bg-white/[0.03] border border-white/[0.04] group">
                      <Paperclip size={14} className="text-white/30" />
                      <a href={fileService.downloadUrl(f.id)} target="_blank" rel="noreferrer"
                        className="text-sm text-brand-400 hover:text-brand-300 flex-1 truncate transition-colors">
                        {f.fileName}
                      </a>
                      <button type="button" onClick={() => deleteFile(f.id)}
                        className="text-white/20 hover:text-red-400 opacity-0 group-hover:opacity-100 transition-all">
                        <Trash2 size={14} />
                      </button>
                    </div>
                  ))}
                </div>
                <label className="cursor-pointer flex items-center gap-2 text-sm text-white/40 hover:text-white/70 transition-colors px-3 py-2 rounded-xl border border-dashed border-white/[0.08] hover:border-white/20">
                  <Upload size={14} />
                  Upload file
                  <input type="file" className="hidden" onChange={handleFileUpload} />
                </label>
              </div>
            )}
          </div>

          {/* Footer */}
          <div className="p-6 border-t border-white/[0.06] flex items-center justify-end gap-3">
            <button type="button" onClick={onClose} className="btn-ghost">Cancel</button>
            <button type="submit" disabled={loading} className="btn-primary flex items-center gap-2">
              {loading ? <div className="w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin" /> : null}
              {task?.id ? 'Update' : 'Create Task'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
