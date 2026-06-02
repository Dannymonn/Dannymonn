import { useState, useEffect, useRef } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Plus, Trash2, Check, ChevronLeft } from 'lucide-react'
import { db } from '../db/database'
import type { Exercise, WorkoutSet } from '../types'
import ExercisePicker from '../components/ExercisePicker'
import RestTimer from '../components/RestTimer'

interface SetRow { reps: string; weight: string; rest: string }
interface ExerciseEntry { exercise: Exercise; sets: WorkoutSet[]; inputs: SetRow }

export default function ActiveWorkout() {
  const { routineId, routineName } = useParams<{ routineId: string; routineName: string }>()
  const navigate = useNavigate()
  const sessionIdRef = useRef<number | null>(null)
  const startTimeRef = useRef(Date.now())
  const [elapsed, setElapsed] = useState(0)
  const [exercises, setExercises] = useState<ExerciseEntry[]>([])
  const [showPicker, setShowPicker] = useState(false)
  const [restTimer, setRestTimer] = useState<number | null>(null)

  useEffect(() => {
    const init = async () => {
      const id = Number(routineId)
      const sid = await db.workoutSessions.add({
        routineId: id > 0 ? id : undefined,
        routineName: decodeURIComponent(routineName ?? 'Free Workout'),
        startTime: startTimeRef.current,
        totalVolume: 0,
        notes: ''
      })
      sessionIdRef.current = sid as number
      if (id > 0) {
        const routineExs = await db.routineExercises.where('routineId').equals(id).sortBy('orderIndex')
        const entries: ExerciseEntry[] = []
        for (const re of routineExs) {
          const ex = await db.exercises.get(re.exerciseId)
          if (ex) entries.push({ exercise: ex, sets: [], inputs: { reps: String(re.targetReps), weight: '', rest: '90' } })
        }
        setExercises(entries)
      }
    }
    init()
    const t = setInterval(() => setElapsed(Math.floor((Date.now() - startTimeRef.current) / 1000)), 1000)
    return () => clearInterval(t)
  }, [])

  const addExercise = (ex: Exercise) => {
    if (exercises.some(e => e.exercise.id === ex.id)) return
    setExercises(prev => [...prev, { exercise: ex, sets: [], inputs: { reps: '', weight: '', rest: '90' } }])
    setShowPicker(false)
  }

  const updateInput = (exId: number, field: keyof SetRow, val: string) => {
    setExercises(prev => prev.map(e => e.exercise.id === exId ? { ...e, inputs: { ...e.inputs, [field]: val } } : e))
  }

  const logSet = async (entry: ExerciseEntry) => {
    const sid = sessionIdRef.current
    if (!sid) return
    const reps = parseInt(entry.inputs.reps)
    const weight = parseFloat(entry.inputs.weight) || 0
    const rest = parseInt(entry.inputs.rest) || 90
    if (!reps) return
    const set: WorkoutSet = {
      sessionId: sid, exerciseId: entry.exercise.id!, exerciseName: entry.exercise.name,
      setNumber: entry.sets.length + 1, reps, weightKg: weight, restSeconds: rest, completedAt: Date.now()
    }
    const setId = await db.workoutSets.add(set) as number
    setExercises(prev => prev.map(e =>
      e.exercise.id === entry.exercise.id ? { ...e, sets: [...e.sets, { ...set, id: setId }] } : e
    ))
    setRestTimer(rest)
  }

  const deleteLastSet = async (exId: number) => {
    setExercises(prev => prev.map(e => {
      if (e.exercise.id !== exId) return e
      const last = e.sets[e.sets.length - 1]
      if (last?.id) db.workoutSets.delete(last.id)
      return { ...e, sets: e.sets.slice(0, -1) }
    }))
  }

  const finish = async () => {
    const sid = sessionIdRef.current
    if (!sid) return
    const allSets = exercises.flatMap(e => e.sets)
    const volume = allSets.reduce((s, set) => s + set.weightKg * set.reps, 0)
    await db.workoutSessions.update(sid, { endTime: Date.now(), totalVolume: volume })
    navigate('/history')
  }

  const mins = Math.floor(elapsed / 60), secs = elapsed % 60
  const fmt = `${String(mins).padStart(2,'0')}:${String(secs).padStart(2,'0')}`

  return (
    <div className="page">
      {restTimer !== null && <RestTimer seconds={restTimer} onDone={() => setRestTimer(null)} />}
      {showPicker && <ExercisePicker onPick={addExercise} onClose={() => setShowPicker(false)} />}

      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <button onClick={() => { if (confirm('Cancel workout?')) navigate(-1) }}>
            <ChevronLeft size={22} className="text-text-muted" />
          </button>
          <h1 className="text-xl font-bold">{decodeURIComponent(routineName ?? 'Workout')}</h1>
        </div>
        <span className="text-accent font-mono font-bold text-lg">{fmt}</span>
      </div>

      {exercises.length === 0 && (
        <p className="text-text-muted text-center py-10">Tap "Add Exercise" to get started</p>
      )}

      {exercises.map(entry => (
        <div key={entry.exercise.id} className="card mb-3">
          <div className="flex justify-between items-center mb-2">
            <div>
              <p className="font-bold">{entry.exercise.name}</p>
              <p className="text-accent text-xs">{entry.exercise.muscleGroup}</p>
            </div>
            <button onClick={() => setExercises(prev => prev.filter(e => e.exercise.id !== entry.exercise.id))}
              className="text-text-muted p-1"><Trash2 size={16} /></button>
          </div>

          {/* Logged sets */}
          {entry.sets.map(s => (
            <div key={s.id} className="flex justify-between text-sm py-1 border-b border-divider">
              <span className="text-text-muted">Set {s.setNumber}</span>
              <span className="font-semibold">{s.reps} reps{s.weightKg > 0 ? ` × ${s.weightKg} kg` : ''}</span>
            </div>
          ))}

          {/* Inputs */}
          <div className={`grid gap-2 mt-3 ${entry.exercise.usesWeight ? 'grid-cols-3' : 'grid-cols-2'}`}>
            <div>
              <label className="text-xs text-text-muted block mb-1">Reps</label>
              <input type="number" inputMode="numeric" value={entry.inputs.reps}
                onChange={e => updateInput(entry.exercise.id!, 'reps', e.target.value)} />
            </div>
            {entry.exercise.usesWeight && (
              <div>
                <label className="text-xs text-text-muted block mb-1">Weight (kg)</label>
                <input type="number" inputMode="decimal" value={entry.inputs.weight}
                  onChange={e => updateInput(entry.exercise.id!, 'weight', e.target.value)} />
              </div>
            )}
            <div>
              <label className="text-xs text-text-muted block mb-1">Rest (s)</label>
              <input type="number" inputMode="numeric" value={entry.inputs.rest}
                onChange={e => updateInput(entry.exercise.id!, 'rest', e.target.value)} />
            </div>
          </div>

          <div className="flex gap-2 mt-3">
            <button className="btn-primary flex-1 flex items-center justify-center gap-1"
              onClick={() => logSet(entry)}>
              <Check size={16} /> Log Set {entry.sets.length + 1}
            </button>
            {entry.sets.length > 0 && (
              <button className="btn-ghost" onClick={() => deleteLastSet(entry.exercise.id!)}>Undo</button>
            )}
          </div>
        </div>
      ))}

      <div className="flex gap-2 mt-2">
        <button className="btn-outline flex-1 flex items-center justify-center gap-1"
          onClick={() => setShowPicker(true)}>
          <Plus size={16} /> Add Exercise
        </button>
        <button className="btn-primary flex-1" onClick={finish}>Finish</button>
      </div>
    </div>
  )
}
