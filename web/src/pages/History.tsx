import { useLiveQuery } from 'dexie-react-hooks'
import { useNavigate } from 'react-router-dom'
import { Trash2 } from 'lucide-react'
import { db } from '../db/database'

export default function History() {
  const navigate = useNavigate()
  const sessions = useLiveQuery(() => db.workoutSessions.orderBy('startTime').reverse().toArray(), [], [])

  const deleteSession = async (id: number) => {
    if (!confirm('Delete this workout?')) return
    await db.workoutSets.where('sessionId').equals(id).delete()
    await db.workoutSessions.delete(id)
  }

  return (
    <div className="page">
      <h1 className="text-2xl font-bold mb-4">Workout History</h1>
      {sessions?.length === 0 && <p className="text-text-muted text-center py-10">No workouts yet.</p>}
      {sessions?.map(s => {
        const duration = s.endTime ? Math.round((s.endTime - s.startTime) / 60000) : null
        return (
          <div key={s.id} className="card mb-2 flex items-center gap-3 cursor-pointer active:bg-white/5"
            onClick={() => navigate(`/history/${s.id}`)}>
            <div className="flex-1">
              <p className="font-bold">{s.routineName}</p>
              <p className="text-text-muted text-xs">{new Date(s.startTime).toLocaleDateString('en-US', { weekday:'short', month:'short', day:'numeric' })}</p>
            </div>
            <div className="text-right">
              {duration && <p className="text-accent text-sm">{duration} min</p>}
              {s.totalVolume > 0 && <p className="text-text-muted text-xs">{Math.round(s.totalVolume)} kg</p>}
            </div>
            <button onClick={e => { e.stopPropagation(); deleteSession(s.id!) }} className="text-text-muted p-1">
              <Trash2 size={16} />
            </button>
          </div>
        )
      })}
    </div>
  )
}
