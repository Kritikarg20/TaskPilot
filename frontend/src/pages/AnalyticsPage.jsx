import React, { useEffect, useState } from 'react'
import { analyticsService } from '../services/api'
import { Bar, Pie, Doughnut } from 'react-chartjs-2'
import toast from 'react-hot-toast'
import {
  Chart as ChartJS,
  CategoryScale, LinearScale, BarElement,
  ArcElement, Title, Tooltip, Legend, PointElement, LineElement
} from 'chart.js'
import { TrendingUp, Target, Clock, Zap } from 'lucide-react'

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend, PointElement, LineElement)

const CHART_OPTIONS = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { labels: { color: 'rgba(255,255,255,0.6)', font: { family: 'DM Sans', size: 12 } } },
    tooltip: {
      backgroundColor: '#22222e',
      borderColor: 'rgba(255,255,255,0.08)',
      borderWidth: 1,
      titleColor: '#fff',
      bodyColor: 'rgba(255,255,255,0.6)',
      padding: 12,
    },
  },
  scales: {
    x: { ticks: { color: 'rgba(255,255,255,0.4)', font: { family: 'DM Sans' } }, grid: { color: 'rgba(255,255,255,0.04)' } },
    y: { ticks: { color: 'rgba(255,255,255,0.4)', font: { family: 'DM Sans' } }, grid: { color: 'rgba(255,255,255,0.04)' } },
  },
}

const PIE_OPTIONS = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'bottom', labels: { color: 'rgba(255,255,255,0.6)', font: { family: 'DM Sans', size: 12 }, padding: 16 } },
    tooltip: {
      backgroundColor: '#22222e',
      borderColor: 'rgba(255,255,255,0.08)',
      borderWidth: 1,
      titleColor: '#fff',
      bodyColor: 'rgba(255,255,255,0.6)',
    },
  },
}

export default function AnalyticsPage() {
  const [analytics, setAnalytics] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    analyticsService.get()
      .then(setAnalytics)
      .catch(() => toast.error('Failed to load analytics'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return (
    <div className="flex items-center justify-center h-full">
      <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
    </div>
  )

  const weeklyBarData = {
    labels: analytics?.weeklyData?.map(d => d.day) || [],
    datasets: [
      {
        label: 'Completed',
        data: analytics?.weeklyData?.map(d => d.completed) || [],
        backgroundColor: 'rgba(99, 102, 241, 0.7)',
        borderRadius: 8,
        borderSkipped: false,
      },
      {
        label: 'Created',
        data: analytics?.weeklyData?.map(d => d.created) || [],
        backgroundColor: 'rgba(99, 102, 241, 0.2)',
        borderRadius: 8,
        borderSkipped: false,
      },
    ],
  }

  const categoryPieData = {
    labels: Object.keys(analytics?.tasksByCategory || {}),
    datasets: [{
      data: Object.values(analytics?.tasksByCategory || {}),
      backgroundColor: ['#6366f1', '#8b5cf6', '#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#ec4899'],
      borderColor: 'transparent',
      hoverOffset: 8,
    }],
  }

  const priorityData = {
    labels: ['High', 'Medium', 'Low'],
    datasets: [{
      data: [
        analytics?.tasksByPriority?.HIGH || 0,
        analytics?.tasksByPriority?.MEDIUM || 0,
        analytics?.tasksByPriority?.LOW || 0,
      ],
      backgroundColor: ['rgba(239, 68, 68, 0.7)', 'rgba(245, 158, 11, 0.7)', 'rgba(16, 185, 129, 0.7)'],
      borderColor: 'transparent',
      borderRadius: 8,
      borderSkipped: false,
    }],
  }

  const completionRate = analytics?.totalTasks > 0
    ? Math.round((analytics.completedTasks / analytics.totalTasks) * 100) : 0

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto">
      <div className="mb-8">
        <h1 className="font-display font-bold text-3xl text-white mb-1">Analytics</h1>
        <p className="text-white/40">Your productivity insights at a glance</p>
      </div>

      {/* Summary cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {[
          { icon: Target, label: 'Completion Rate', value: `${completionRate}%`, color: 'text-brand-400', bg: 'bg-brand-500/15' },
          { icon: TrendingUp, label: 'Total Tasks', value: analytics?.totalTasks || 0, color: 'text-emerald-400', bg: 'bg-emerald-500/15' },
          { icon: Zap, label: 'Completed', value: analytics?.completedTasks || 0, color: 'text-blue-400', bg: 'bg-blue-500/15' },
          { icon: Clock, label: 'Overdue', value: analytics?.overdueTasksCount || 0, color: 'text-red-400', bg: 'bg-red-500/15' },
        ].map(({ icon: Icon, label, value, color, bg }) => (
          <div key={label} className="card flex items-center gap-4">
            <div className={`w-11 h-11 rounded-2xl flex items-center justify-center flex-shrink-0 ${bg}`}>
              <Icon size={20} className={color} />
            </div>
            <div>
              <p className="text-2xl font-display font-bold text-white">{value}</p>
              <p className="text-xs text-white/40">{label}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Charts row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        {/* Weekly Bar */}
        <div className="card">
          <h3 className="font-display font-semibold text-white mb-5">Weekly Activity</h3>
          <div className="h-56">
            <Bar data={weeklyBarData} options={CHART_OPTIONS} />
          </div>
        </div>

        {/* Priority Bar */}
        <div className="card">
          <h3 className="font-display font-semibold text-white mb-5">Tasks by Priority</h3>
          <div className="h-56">
            <Bar data={priorityData} options={{ ...CHART_OPTIONS, plugins: { ...CHART_OPTIONS.plugins, legend: { display: false } } }} />
          </div>
        </div>
      </div>

      {/* Charts row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Category Pie */}
        <div className="card">
          <h3 className="font-display font-semibold text-white mb-5">Tasks by Category</h3>
          {Object.keys(analytics?.tasksByCategory || {}).length === 0 ? (
            <p className="text-white/30 text-sm text-center py-10">No categorized tasks yet</p>
          ) : (
            <div className="h-56">
              <Pie data={categoryPieData} options={PIE_OPTIONS} />
            </div>
          )}
        </div>

        {/* Status breakdown */}
        <div className="card">
          <h3 className="font-display font-semibold text-white mb-5">Task Status Breakdown</h3>
          <div className="space-y-4 pt-2">
            {[
              { label: 'Completed', value: analytics?.completedTasks || 0, color: 'bg-emerald-500', max: analytics?.totalTasks },
              { label: 'In Progress', value: analytics?.inProgressTasks || 0, color: 'bg-blue-500', max: analytics?.totalTasks },
              { label: 'Pending', value: analytics?.pendingTasks || 0, color: 'bg-white/20', max: analytics?.totalTasks },
            ].map(({ label, value, color, max }) => (
              <div key={label}>
                <div className="flex items-center justify-between mb-1.5">
                  <span className="text-sm text-white/60">{label}</span>
                  <span className="text-sm font-medium text-white">{value}</span>
                </div>
                <div className="h-2 bg-white/[0.06] rounded-full overflow-hidden">
                  <div
                    className={`h-full ${color} rounded-full transition-all duration-700`}
                    style={{ width: max > 0 ? `${(value / max) * 100}%` : '0%' }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
