import { useLiveQuery } from 'dexie-react-hooks'
import { useParams, useNavigate } from 'react-router-dom'
import { ChevronLeft } from 'lucide-react'
import { db } from '../db/database'

export default function WorkoutDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const session = useLiveQuery(() => db.workoutSessions.get(Number(id)), [id])
  const sets = useLiveQuery(() => db.workoutSets.where('sessionId').equals(Number(id)).toArray(), [id], [])

  // Group sets by exercise
  const byExercise = sets?.reduce<Record<string, typeof sets>>((acc, s) => {
    acc[s.exerciseName] = [...(acc[s.exerciseName] ?? []), s]
    return acc
  }, {}) ?? {}

  if (!session) return null
  const duration = session.endTime ? Math.round((session.endTime - session.startTime) / 60000) : null

  return (
    <div className="page">
      <div className="flex items-center gap-2 mb-4">
        <button onClick={() => navigate(-1)}><ChevronLeft size={22} className="text-text-muted" /></button>
        <h1 className="text-xl font-bold">{session.routineName}</h1>
      </div>
      <div className="flex gap-4 text-sm text-text-muted mb-4">
        <span>{new Date(session.startTime).toLocaleDateString()}</span>
        {duration && <span className="text-accent">{duration} min</span>}
        {session.totalVolume > 0 && <span>{Math.round(session.totalVolume)} kg total</span>}
      </div>

      {Object.entries(byExercise).map(([name, exSets]) => (
        <div key={name} className="card mb-3">
          <p className="font-bold mb-2">{name}</p>
          {exSets.map(s => (
            <div key={s.id} className="flex justify-between py-1.5 border-b border-divider last:border-0 text-sm">
              <span className="text-text-muted">Set {s.setNumber}</span>
              <span className="font-semibold">
                {s.reps} reps{s.weightKg > 0 ? ` × ${s.weightKg} kg` : ''}
              </span>
            </div>
          ))}
        </div>
      ))}
    </div>
  )
}
