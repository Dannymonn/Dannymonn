import { useLiveQuery } from 'dexie-react-hooks'
import { useNavigate } from 'react-router-dom'
import { Plus, Pencil, Trash2, Play } from 'lucide-react'
import { db } from '../db/database'

export default function Routines() {
  const navigate = useNavigate()
  const routines = useLiveQuery(() => db.routines.orderBy('createdAt').reverse().toArray(), [], [])

  const deleteRoutine = async (id: number) => {
    if (!confirm('Delete this routine?')) return
    await db.routineExercises.where('routineId').equals(id).delete()
    await db.routines.delete(id)
  }

  return (
    <div className="page">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">Routines</h1>
        <button className="btn-primary flex items-center gap-1" onClick={() => navigate('/routines/edit/new')}>
          <Plus size={16} /> New
        </button>
      </div>

      {routines?.length === 0 && (
        <p className="text-text-muted text-center py-10">No routines yet. Create one to get started.</p>
      )}

      {routines?.map(r => (
        <div key={r.id} className="card mb-3 flex items-center gap-3">
          <div className="flex-1">
            <p className="font-bold">{r.name}</p>
            {r.description && <p className="text-text-muted text-sm">{r.description}</p>}
          </div>
          <button onClick={() => navigate(`/workout/${r.id}/${encodeURIComponent(r.name)}`)}
            className="text-accent p-2"><Play size={20} /></button>
          <button onClick={() => navigate(`/routines/edit/${r.id}`)}
            className="text-text-muted p-2"><Pencil size={18} /></button>
          <button onClick={() => deleteRoutine(r.id!)}
            className="text-accent-red p-2"><Trash2 size={18} /></button>
        </div>
      ))}
    </div>
  )
}
