import React, { useState, useEffect } from 'react'
import { userService, categoryService } from '../services/api'
import { useAuth } from '../context/AuthContext'
import toast from 'react-hot-toast'
import { User, Mail, Edit2, Plus, Trash2, Check, X } from 'lucide-react'

const PRESET_COLORS = ['#6366f1', '#ef4444', '#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#ec4899', '#14b8a6']

export default function ProfilePage() {
  const { user: authUser } = useAuth()
  const [profile, setProfile] = useState(null)
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [editing, setEditing] = useState(false)
  const [form, setForm] = useState({ name: '', avatarUrl: '' })
  const [newCat, setNewCat] = useState({ name: '', color: '#6366f1', icon: '📚' })
  const [showNewCat, setShowNewCat] = useState(false)

  const load = async () => {
    try {
      const [p, c] = await Promise.all([userService.getProfile(), categoryService.getAll()])
      setProfile(p)
      setCategories(c)
      setForm({ name: p.name, avatarUrl: p.avatarUrl || '' })
    } catch { toast.error('Failed to load profile') }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  const saveProfile = async () => {
    try {
      const updated = await userService.updateProfile(form)
      setProfile(updated)
      setEditing(false)
      toast.success('Profile updated')
    } catch { toast.error('Failed to update') }
  }

  const addCategory = async () => {
    if (!newCat.name.trim()) return
    try {
      const cat = await categoryService.create(newCat)
      setCategories(p => [...p, cat])
      setNewCat({ name: '', color: '#6366f1', icon: '📚' })
      setShowNewCat(false)
      toast.success('Category created')
    } catch { toast.error('Failed to create category') }
  }

  const deleteCategory = async (id) => {
    if (!confirm('Delete this category?')) return
    try {
      await categoryService.delete(id)
      setCategories(p => p.filter(c => c.id !== id))
      toast.success('Category deleted')
    } catch { toast.error('Failed to delete') }
  }

  if (loading) return (
    <div className="flex items-center justify-center h-full">
      <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
    </div>
  )

  return (
    <div className="p-6 lg:p-8 max-w-3xl mx-auto">
      <h1 className="font-display font-bold text-3xl text-white mb-8">Profile</h1>

      {/* Profile Card */}
      <div className="card mb-6">
        <div className="flex items-start gap-5">
          <div className="w-16 h-16 rounded-2xl bg-brand-500/30 flex items-center justify-center text-brand-400 text-2xl font-display font-bold flex-shrink-0">
            {profile?.name?.charAt(0).toUpperCase()}
          </div>
          <div className="flex-1">
            {editing ? (
              <div className="space-y-3">
                <input
                  value={form.name}
                  onChange={e => setForm(p => ({ ...p, name: e.target.value }))}
                  placeholder="Your name"
                  className="input-field"
                />
                <div className="flex gap-2">
                  <button onClick={saveProfile} className="btn-primary flex items-center gap-2">
                    <Check size={16} /> Save
                  </button>
                  <button onClick={() => setEditing(false)} className="btn-ghost flex items-center gap-2">
                    <X size={16} /> Cancel
                  </button>
                </div>
              </div>
            ) : (
              <>
                <div className="flex items-center gap-3 mb-1">
                  <h2 className="font-display font-bold text-xl text-white">{profile?.name}</h2>
                  <button onClick={() => setEditing(true)} className="btn-ghost p-1.5 rounded-lg text-white/40">
                    <Edit2 size={15} />
                  </button>
                </div>
                <div className="flex items-center gap-2 text-white/40 text-sm">
                  <Mail size={14} />
                  {profile?.email}
                </div>
              </>
            )}
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-4 mt-6 pt-5 border-t border-white/[0.06]">
          {[
            { label: 'Total Tasks', value: profile?.totalTasks || 0 },
            { label: 'Completed', value: profile?.completedTasks || 0 },
            { label: 'Completion Rate', value: profile?.totalTasks > 0 ? `${Math.round((profile.completedTasks / profile.totalTasks) * 100)}%` : '0%' },
          ].map(({ label, value }) => (
            <div key={label} className="text-center">
              <p className="font-display font-bold text-2xl text-white">{value}</p>
              <p className="text-xs text-white/40 mt-0.5">{label}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Categories */}
      <div className="card">
        <div className="flex items-center justify-between mb-5">
          <h3 className="font-display font-semibold text-lg text-white">Categories</h3>
          <button onClick={() => setShowNewCat(!showNewCat)} className="btn-ghost flex items-center gap-2 text-sm">
            <Plus size={16} /> Add
          </button>
        </div>

        {showNewCat && (
          <div className="mb-4 p-4 rounded-xl bg-white/[0.03] border border-white/[0.08] space-y-3">
            <div className="flex gap-3">
              <input
                value={newCat.icon}
                onChange={e => setNewCat(p => ({ ...p, icon: e.target.value }))}
                placeholder="🎓"
                className="input-field w-16 text-center text-lg"
              />
              <input
                value={newCat.name}
                onChange={e => setNewCat(p => ({ ...p, name: e.target.value }))}
                placeholder="Category name"
                className="input-field flex-1"
              />
            </div>
            <div className="flex items-center gap-2">
              <span className="text-xs text-white/40">Color:</span>
              <div className="flex gap-2">
                {PRESET_COLORS.map(c => (
                  <button
                    key={c}
                    onClick={() => setNewCat(p => ({ ...p, color: c }))}
                    className={`w-6 h-6 rounded-full transition-transform ${newCat.color === c ? 'scale-125 ring-2 ring-white/50' : 'hover:scale-110'}`}
                    style={{ backgroundColor: c }}
                  />
                ))}
              </div>
            </div>
            <div className="flex gap-2">
              <button onClick={addCategory} className="btn-primary text-sm py-2">Create</button>
              <button onClick={() => setShowNewCat(false)} className="btn-ghost text-sm py-2">Cancel</button>
            </div>
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {categories.map(cat => (
            <div key={cat.id}
              className="flex items-center gap-3 p-3 rounded-xl bg-white/[0.03] border border-white/[0.05] group hover:border-white/[0.10] transition-colors">
              <div className="w-8 h-8 rounded-lg flex items-center justify-center text-lg flex-shrink-0"
                style={{ backgroundColor: cat.color + '22', border: `1px solid ${cat.color}44` }}>
                {cat.icon}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-white">{cat.name}</p>
                <p className="text-xs text-white/40">{cat.taskCount} tasks</p>
              </div>
              <div className="w-3 h-3 rounded-full flex-shrink-0" style={{ backgroundColor: cat.color }} />
              <button onClick={() => deleteCategory(cat.id)}
                className="text-white/20 hover:text-red-400 opacity-0 group-hover:opacity-100 transition-all ml-1">
                <Trash2 size={15} />
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
