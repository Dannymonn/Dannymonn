import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Plus, Trash2, ChevronLeft } from 'lucide-react'
import { db } from '../db/database'
import type { RoutineExercise, Exercise } from '../types'
import ExercisePicker from '../components/ExercisePicker'

interface EditableRE extends RoutineExercise { exercise?: Exercise }

export default function EditRoutine() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const isNew = id === 'new'
  const [name, setName] = useState('')
  const [desc, setDesc] = useState('')
  const [exercises, setExercises] = useState<EditableRE[]>([])
  const [showPicker, setShowPicker] = useState(false)

  useEffect(() => {
    if (isNew) return
    const load = async () => {
      const r = await db.routines.get(Number(id))
      if (!r) return
      setName(r.name); setDesc(r.description)
      const res = await db.routineExercises.where('routineId').equals(Number(id)).sortBy('orderIndex')
      const withEx = await Promise.all(res.map(async re => ({ ...re, exercise: await db.exercises.get(re.exerciseId) })))
      setExercises(withEx)
    }
    load()
  }, [id])

  const addExercise = (ex: Exercise) => {
    if (exercises.some(e => e.exerciseId === ex.id)) return
    setExercises(prev => [...prev, {
      id: undefined, routineId: 0, exerciseId: ex.id!, exerciseName: ex.name,
      targetSets: 3, targetReps: 10, orderIndex: prev.length, exercise: ex
    }])
    setShowPicker(false)
  }

  const save = async () => {
    if (!name.trim()) { alert('Name is required'); return }
    let routineId = Number(id)
    if (isNew) {
      routineId = await db.routines.add({ name: name.trim(), description: desc.trim(), createdAt: Date.now() }) as number
    } else {
      await db.routines.update(routineId, { name: name.trim(), description: desc.trim() })
      await db.routineExercises.where('routineId').equals(routineId).delete()
    }
    await db.routineExercises.bulkAdd(exercises.map((e, i) => ({
      routineId, exerciseId: e.exerciseId, exerciseName: e.exerciseName,
      targetSets: e.targetSets, targetReps: e.targetReps, orderIndex: i
    })))
    navigate('/routines')
  }

  return (
    <div className="page">
      {showPicker && <ExercisePicker onPick={addExercise} onClose={() => setShowPicker(false)} />}

      <div className="flex items-center gap-2 mb-4">
        <button onClick={() => navigate(-1)}><ChevronLeft size={22} className="text-text-muted" /></button>
        <h1 className="text-xl font-bold">{isNew ? 'New Routine' : 'Edit Routine'}</h1>
      </div>

      <div className="mb-3">
        <label className="text-xs text-text-muted block mb-1">Routine name</label>
        <input value={name} onChange={e => setName(e.target.value)} placeholder="e.g. Push Day" />
      </div>
      <div className="mb-4">
        <label className="text-xs text-text-muted block mb-1">Description (optional)</label>
        <input value={desc} onChange={e => setDesc(e.target.value)} placeholder="Chest, shoulders, triceps" />
      </div>

      <div className="flex justify-between items-center mb-2">
        <h2 className="font-semibold">Exercises</h2>
        <button className="btn-outline flex items-center gap-1 py-1.5 px-3 text-sm"
          onClick={() => setShowPicker(true)}><Plus size={14} /> Add</button>
      </div>

      {exercises.map((e, i) => (
        <div key={i} className="card mb-2 flex items-center gap-3">
          <div className="flex-1">
            <p className="font-medium">{e.exerciseName}</p>
            <div className="flex gap-3 mt-1">
              <div className="flex items-center gap-1">
                <span className="text-xs text-text-muted">Sets</span>
                <input type="number" inputMode="numeric" className="w-14 py-1 px-2 text-sm"
                  value={e.targetSets}
                  onChange={ev => setExercises(prev => prev.map((x, j) => j === i ? { ...x, targetSets: parseInt(ev.target.value) || 1 } : x))} />
              </div>
              <div className="flex items-center gap-1">
                <span className="text-xs text-text-muted">Reps</span>
                <input type="number" inputMode="numeric" className="w-14 py-1 px-2 text-sm"
                  value={e.targetReps}
                  onChange={ev => setExercises(prev => prev.map((x, j) => j === i ? { ...x, targetReps: parseInt(ev.target.value) || 1 } : x))} />
              </div>
            </div>
          </div>
          <button onClick={() => setExercises(prev => prev.filter((_, j) => j !== i))}
            className="text-accent-red p-1"><Trash2 size={16} /></button>
        </div>
      ))}

      <button className="btn-primary w-full mt-4" onClick={save}>Save Routine</button>
    </div>
  )
}
