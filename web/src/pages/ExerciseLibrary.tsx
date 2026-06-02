import { useState } from 'react'
import { useLiveQuery } from 'dexie-react-hooks'
import { Plus, Search, Trash2 } from 'lucide-react'
import { db } from '../db/database'
import type { MuscleGroup, ExerciseCategory } from '../types'

const MUSCLES: MuscleGroup[] = ['CHEST','BACK','SHOULDERS','BICEPS','TRICEPS','LEGS','GLUTES','CORE','CARDIO','OTHER']
const CATS: ExerciseCategory[] = ['BARBELL','DUMBBELL','MACHINE','CABLE','BODYWEIGHT','CARDIO','OTHER']

export default function ExerciseLibrary() {
  const [query, setQuery] = useState('')
  const [showAdd, setShowAdd] = useState(false)
  const [newName, setNewName] = useState('')
  const [newMuscle, setNewMuscle] = useState<MuscleGroup>('CHEST')
  const [newCat, setNewCat] = useState<ExerciseCategory>('BARBELL')
  const [usesWeight, setUsesWeight] = useState(true)

  const exercises = useLiveQuery(async () => {
    const all = await db.exercises.toArray()
    if (!query) return all.sort((a, b) => a.name.localeCompare(b.name))
    return all.filter(e => e.name.toLowerCase().includes(query.toLowerCase()))
      .sort((a, b) => a.name.localeCompare(b.name))
  }, [query], [])

  const addExercise = async () => {
    if (!newName.trim()) return
    await db.exercises.add({ name: newName.trim(), muscleGroup: newMuscle, category: newCat, usesWeight, isCustom: true })
    setNewName(''); setShowAdd(false)
  }

  const deleteExercise = async (id: number, isCustom: boolean) => {
    if (!isCustom) { alert('Cannot delete built-in exercises'); return }
    if (confirm('Delete this exercise?')) await db.exercises.delete(id)
  }

  return (
    <div className="page">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">Exercises</h1>
        <button className="btn-primary flex items-center gap-1" onClick={() => setShowAdd(!showAdd)}>
          <Plus size={16} /> Custom
        </button>
      </div>

      {showAdd && (
        <div className="card mb-4 flex flex-col gap-3">
          <input value={newName} onChange={e => setNewName(e.target.value)} placeholder="Exercise name" />
          <select value={newMuscle} onChange={e => setNewMuscle(e.target.value as MuscleGroup)}>
            {MUSCLES.map(m => <option key={m} value={m}>{m}</option>)}
          </select>
          <select value={newCat} onChange={e => setNewCat(e.target.value as ExerciseCategory)}>
            {CATS.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
          <label className="flex items-center gap-2 text-sm">
            <input type="checkbox" checked={usesWeight} onChange={e => setUsesWeight(e.target.checked)} className="w-auto" />
            Uses weight
          </label>
          <button className="btn-primary" onClick={addExercise}>Add Exercise</button>
        </div>
      )}

      <div className="relative mb-3">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-text-muted" />
        <input className="pl-9" placeholder="Search exercises..." value={query} onChange={e => setQuery(e.target.value)} />
      </div>

      <div className="flex flex-col gap-1">
        {exercises?.map(ex => (
          <div key={ex.id} className="card py-3 flex items-center">
            <div className="flex-1">
              <div className="flex items-center gap-2">
                <span className="font-medium">{ex.name}</span>
                {ex.isCustom && <span className="text-xs text-accent-red border border-accent-red rounded px-1">CUSTOM</span>}
              </div>
              <span className="text-xs text-accent">{ex.muscleGroup} · {ex.category}</span>
            </div>
            {ex.isCustom && (
              <button onClick={() => deleteExercise(ex.id!, ex.isCustom)} className="text-text-muted p-1">
                <Trash2 size={16} />
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
