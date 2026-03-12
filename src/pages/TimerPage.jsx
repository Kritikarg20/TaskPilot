import React, { useState, useEffect, useRef } from 'react'
import { Play, Pause, RotateCcw, Coffee, Brain } from 'lucide-react'

const MODES = [
  { id: 'focus', label: 'Focus', minutes: 25, icon: Brain, color: '#6366f1' },
  { id: 'short', label: 'Short Break', minutes: 5, icon: Coffee, color: '#10b981' },
  { id: 'long', label: 'Long Break', minutes: 15, icon: Coffee, color: '#3b82f6' },
]

export default function TimerPage() {
  const [modeIdx, setModeIdx] = useState(0)
  const [timeLeft, setTimeLeft] = useState(MODES[0].minutes * 60)
  const [running, setRunning] = useState(false)
  const [sessions, setSessions] = useState(0)
  const intervalRef = useRef(null)

  const mode = MODES[modeIdx]
  const total = mode.minutes * 60
  const progress = (total - timeLeft) / total
  const radius = 120
  const circumference = 2 * Math.PI * radius
  const strokeOffset = circumference - (progress * circumference)

  useEffect(() => {
    if (running) {
      intervalRef.current = setInterval(() => {
        setTimeLeft(p => {
          if (p <= 1) {
            clearInterval(intervalRef.current)
            setRunning(false)
            if (modeIdx === 0) setSessions(s => s + 1)
            new Audio('data:audio/wav;base64,UklGRl9vT19XQVZFZm10IBAAAA...').play?.().catch?.(() => {})
            return 0
          }
          return p - 1
        })
      }, 1000)
    }
    return () => clearInterval(intervalRef.current)
  }, [running])

  const switchMode = (idx) => {
    setModeIdx(idx)
    setRunning(false)
    clearInterval(intervalRef.current)
    setTimeLeft(MODES[idx].minutes * 60)
  }

  const reset = () => {
    setRunning(false)
    clearInterval(intervalRef.current)
    setTimeLeft(mode.minutes * 60)
  }

  const toggle = () => setRunning(p => !p)

  const mins = String(Math.floor(timeLeft / 60)).padStart(2, '0')
  const secs = String(timeLeft % 60).padStart(2, '0')

  return (
    <div className="p-6 lg:p-8 max-w-2xl mx-auto">
      <div className="mb-8">
        <h1 className="font-display font-bold text-3xl text-white mb-1">Pomodoro</h1>
        <p className="text-white/40">Stay focused with Pomodoro technique</p>
      </div>

      {/* Mode selector */}
      <div className="flex gap-2 mb-10 p-1.5 rounded-2xl bg-white/[0.04] border border-white/[0.06]">
        {MODES.map((m, i) => (
          <button
            key={m.id}
            onClick={() => switchMode(i)}
            className={`flex-1 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 ${
              modeIdx === i ? 'text-white shadow-lg' : 'text-white/40 hover:text-white/70'
            }`}
            style={modeIdx === i ? { backgroundColor: m.color + '33', border: `1px solid ${m.color}55` } : {}}
          >
            {m.label}
          </button>
        ))}
      </div>

      {/* Circular Timer */}
      <div className="flex flex-col items-center mb-10">
        <div className={`relative ${running ? 'timer-active' : ''} rounded-full`}
          style={{ borderRadius: '50%' }}>
          <svg width="280" height="280" className="rotate-[-90deg]">
            {/* Background circle */}
            <circle
              cx="140" cy="140" r={radius}
              fill="none"
              stroke="rgba(255,255,255,0.05)"
              strokeWidth="8"
            />
            {/* Progress circle */}
            <circle
              cx="140" cy="140" r={radius}
              fill="none"
              stroke={mode.color}
              strokeWidth="8"
              strokeLinecap="round"
              strokeDasharray={circumference}
              strokeDashoffset={strokeOffset}
              style={{ transition: 'stroke-dashoffset 1s linear' }}
            />
          </svg>
          <div className="absolute inset-0 flex flex-col items-center justify-center">
            <div className="font-mono text-6xl font-bold text-white tracking-tight">
              {mins}:{secs}
            </div>
            <p className="text-white/40 text-sm mt-1">{mode.label}</p>
          </div>
        </div>
      </div>

      {/* Controls */}
      <div className="flex items-center justify-center gap-4 mb-8">
        <button
          onClick={reset}
          className="w-12 h-12 rounded-full bg-white/[0.06] hover:bg-white/[0.10] text-white/60 hover:text-white flex items-center justify-center transition-all"
        >
          <RotateCcw size={20} />
        </button>
        <button
          onClick={toggle}
          className="w-16 h-16 rounded-full flex items-center justify-center text-white shadow-lg transition-all active:scale-95"
          style={{ backgroundColor: mode.color, boxShadow: `0 0 30px ${mode.color}40` }}
        >
          {running ? <Pause size={24} /> : <Play size={24} className="ml-1" />}
        </button>
        <div className="w-12 h-12" /> {/* Spacer */}
      </div>

      {/* Sessions */}
      <div className="card text-center">
        <p className="text-white/40 text-sm mb-3">Sessions completed today</p>
        <div className="flex items-center justify-center gap-2 mb-2">
          {Array.from({ length: Math.max(sessions, 4) }).map((_, i) => (
            <div
              key={i}
              className={`w-8 h-8 rounded-full flex items-center justify-center text-sm transition-all
                ${i < sessions ? 'bg-brand-500 text-white shadow-lg shadow-brand-500/30' : 'bg-white/[0.06] text-white/20'}`}
            >
              {i < sessions ? '✓' : '○'}
            </div>
          ))}
        </div>
        <p className="font-display font-bold text-3xl text-white mt-2">{sessions}</p>
        <p className="text-white/30 text-xs mt-1">Every 4 sessions = take a long break!</p>
      </div>

      {/* Tips */}
      <div className="mt-6 card">
        <h3 className="font-semibold text-white mb-3">Pomodoro Technique</h3>
        <ul className="space-y-2 text-sm text-white/50">
          <li className="flex items-start gap-2"><span className="text-brand-400 flex-shrink-0">1.</span> Work for 25 minutes</li>
          <li className="flex items-start gap-2"><span className="text-brand-400 flex-shrink-0">2.</span> Take a 5 minute short break</li>
          <li className="flex items-start gap-2"><span className="text-brand-400 flex-shrink-0">3.</span> Repeat 4 times</li>
          <li className="flex items-start gap-2"><span className="text-brand-400 flex-shrink-0">4.</span> Take a 15-30 minute long break</li>
        </ul>
      </div>
    </div>
  )
}
