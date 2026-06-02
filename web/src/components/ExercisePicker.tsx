import { useState } from 'react'
import { useLiveQuery } from 'dexie-react-hooks'
import { X, Search } from 'lucide-react'
import { db } from '../db/database'
import type { Exercise, MuscleGroup } from '../types'

const MUSCLES: MuscleGroup[] = ['CHEST','BACK','SHOULDERS','BICEPS','TRICEPS','LEGS','GLUTES','CORE','CARDIO','OTHER']

interface Props { onPick: (e: Exercise) => void; onClose: () => void }

export default function ExercisePicker({ onPick, onClose }: Props) {
  const [query, setQuery] = useState('')
  const [muscle, setMuscle] = useState<MuscleGroup | ''>('')

  const exercises = useLiveQuery(async () => {
    let all = await db.exercises.toArray()
    if (muscle) all = all.filter(e => e.muscleGroup === muscle)
    if (query) all = all.filter(e => e.name.toLowerCase().includes(query.toLowerCase()))
    return all.sort((a, b) => a.name.localeCompare(b.name))
  }, [query, muscle], [])

  return (
    <div className="fixed inset-0 bg-black/70 z-50 flex items-end">
      <div className="bg-surface w-full rounded-t-2xl max-h-[80vh] flex flex-col">
        <div className="flex items-center justify-between p-4 border-b border-divider">
          <h2 className="text-lg font-bold">Pick Exercise</h2>
          <button onClick={onClose}><X size={22} className="text-text-muted" /></button>
        </div>
        <div className="p-3 flex gap-2">
          <div className="flex-1 relative">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-text-muted" />
            <input className="pl-9 py-2" placeholder="Search..." value={query} onChange={e => setQuery(e.target.value)} />
          </div>
          <select className="w-36 py-2 text-sm" value={muscle} onChange={e => setMuscle(e.target.value as MuscleGroup | '')}>
            <option value="">All muscles</option>
            {MUSCLES.map(m => <option key={m} value={m}>{m}</option>)}
          </select>
        </div>
        <div className="overflow-y-auto flex-1">
          {exercises?.map(ex => (
            <button key={ex.id} onClick={() => onPick(ex)}
              className="w-full text-left px-4 py-3 border-b border-divider hover:bg-white/5 flex justify-between items-center">
              <span className="font-medium">{ex.name}</span>
              <span className="text-xs text-accent">{ex.muscleGroup}</span>
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}
