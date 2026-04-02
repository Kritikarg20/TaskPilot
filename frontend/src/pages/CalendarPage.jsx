import React, { useState, useEffect } from 'react'
import { taskService } from '../services/api'
import TaskModal from '../components/tasks/TaskModal'
import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import toast from 'react-hot-toast'

const PRIORITY_COLORS = {
  HIGH: '#ef4444',
  MEDIUM: '#f59e0b',
  LOW: '#10b981',
}

export default function CalendarPage() {
  const [tasks, setTasks] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editTask, setEditTask] = useState(null)

  const load = async () => {
    try {
      const t = await taskService.getAll()
      setTasks(t)
    } catch { toast.error('Failed to load tasks') }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  const events = tasks
    .filter(t => t.deadline)
    .map(t => ({
      id: t.id,
      title: t.title,
      date: t.deadline,
      backgroundColor: PRIORITY_COLORS[t.priority] || '#6366f1',
      borderColor: PRIORITY_COLORS[t.priority] || '#6366f1',
      extendedProps: { task: t },
      classNames: t.status === 'COMPLETED' ? ['opacity-50 line-through'] : [],
    }))

  const handleEventClick = ({ event }) => {
    setEditTask(event.extendedProps.task)
    setShowModal(true)
  }

  if (loading) return (
    <div className="flex items-center justify-center h-full">
      <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
    </div>
  )

  return (
    <div className="p-6 lg:p-8 h-full flex flex-col">
      <div className="mb-8">
        <h1 className="font-display font-bold text-3xl text-white mb-1">Calendar</h1>
        <p className="text-white/40">View tasks by deadline</p>
      </div>

      <div className="card flex-1">
        <FullCalendar
          plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
          initialView="dayGridMonth"
          headerToolbar={{
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay',
          }}
          events={events}
          eventClick={handleEventClick}
          height="100%"
          eventDisplay="block"
          dayMaxEvents={3}
        />
      </div>

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
