import React, { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { analyticsService, taskService } from '../services/api'
import { Link } from 'react-router-dom'
import { CheckCircle2, Clock, AlertCircle, Flame, ArrowRight, Calendar } from 'lucide-react'
import { format, isToday, isTomorrow, parseISO } from 'date-fns'

function StatCard({ icon: Icon, label, value, color, bg }) {
  return (
    <div className="card flex items-center gap-4">
      <div className={`w-12 h-12 rounded-2xl flex items-center justify-center flex-shrink-0 ${bg}`}>
        <Icon size={22} className={color} />
      </div>
      <div>
        <p className="text-3xl font-display font-bold text-white">{value}</p>
        <p className="text-xs text-white/40 mt-0.5">{label}</p>
      </div>
    </div>
  )
}

function DeadlineTag({ deadline }) {
  const date = parseISO(deadline)
  if (isToday(date)) return <span className="badge bg-red-500/15 text-red-400 border border-red-500/20">Today</span>
  if (isTomorrow(date)) return <span className="badge bg-amber-500/15 text-amber-400 border border-amber-500/20">Tomorrow</span>
  return <span className="text-white/40 text-xs">{format(date, 'MMM d')}</span>
}

export default function DashboardPage() {
  const { user } = useAuth()
  const [analytics, setAnalytics] = useState(null)
  const [recentTasks, setRecentTasks] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([analyticsService.get(), taskService.getAll()])
      .then(([a, tasks]) => {
        setAnalytics(a)
        setRecentTasks(tasks.slice(0, 5))
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  const hour = new Date().getHours()
  const greeting = hour < 12 ? 'Good morning' : hour < 18 ? 'Good afternoon' : 'Good evening'

  if (loading) return (
    <div className="flex items-center justify-center h-full">
      <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
    </div>
  )

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <h1 className="font-display font-bold text-3xl text-white mb-1">
          {greeting}, {user?.name?.split(' ')[0]} 👋
        </h1>
        <p className="text-white/40">Here's your productivity overview for today.</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard icon={CheckCircle2} label="Completed Tasks" value={analytics?.completedTasks ?? 0}
          color="text-emerald-400" bg="bg-emerald-500/15" />
        <StatCard icon={Clock} label="In Progress" value={analytics?.inProgressTasks ?? 0}
          color="text-blue-400" bg="bg-blue-500/15" />
        <StatCard icon={AlertCircle} label="Pending" value={analytics?.pendingTasks ?? 0}
          color="text-amber-400" bg="bg-amber-500/15" />
        <StatCard icon={Flame} label="High Priority" value={analytics?.highPriorityTasks ?? 0}
          color="text-red-400" bg="bg-red-500/15" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Upcoming Deadlines */}
        <div className="card">
          <div className="flex items-center justify-between mb-5">
            <h2 className="font-display font-semibold text-lg text-white">Upcoming Deadlines</h2>
            <Link to="/calendar" className="text-brand-400 text-sm hover:text-brand-300 flex items-center gap-1">
              View all <ArrowRight size={14} />
            </Link>
          </div>
          {analytics?.upcomingDeadlines?.length === 0 ? (
            <p className="text-white/30 text-sm text-center py-6">No upcoming deadlines 🎉</p>
          ) : (
            <div className="space-y-3">
              {analytics?.upcomingDeadlines?.map(task => (
                <div key={task.id} className="flex items-center gap-3 p-3 rounded-xl bg-white/[0.03] border border-white/[0.04] hover:border-white/[0.08] transition-colors">
                  <Calendar size={16} className="text-white/30 flex-shrink-0" />
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-white truncate">{task.title}</p>
                    <p className="text-xs text-white/40">{task.category}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className={`badge ${task.priority === 'HIGH' ? 'priority-high' : task.priority === 'MEDIUM' ? 'priority-medium' : 'priority-low'}`}>
                      {task.priority}
                    </span>
                    {task.deadline && <DeadlineTag deadline={task.deadline} />}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Recent Tasks */}
        <div className="card">
          <div className="flex items-center justify-between mb-5">
            <h2 className="font-display font-semibold text-lg text-white">Recent Tasks</h2>
            <Link to="/tasks" className="text-brand-400 text-sm hover:text-brand-300 flex items-center gap-1">
              View all <ArrowRight size={14} />
            </Link>
          </div>
          {recentTasks.length === 0 ? (
            <p className="text-white/30 text-sm text-center py-6">No tasks yet. Create one!</p>
          ) : (
            <div className="space-y-3">
              {recentTasks.map(task => (
                <div key={task.id} className="flex items-center gap-3 p-3 rounded-xl bg-white/[0.03] border border-white/[0.04] hover:border-white/[0.08] transition-colors">
                  <div className={`w-2 h-2 rounded-full flex-shrink-0 ${
                    task.status === 'COMPLETED' ? 'bg-emerald-400' :
                    task.status === 'IN_PROGRESS' ? 'bg-blue-400' : 'bg-white/20'
                  }`} />
                  <p className={`text-sm flex-1 truncate ${task.status === 'COMPLETED' ? 'line-through text-white/30' : 'text-white'}`}>
                    {task.title}
                  </p>
                  <span className={`badge ${task.priority === 'HIGH' ? 'priority-high' : task.priority === 'MEDIUM' ? 'priority-medium' : 'priority-low'}`}>
                    {task.priority}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
