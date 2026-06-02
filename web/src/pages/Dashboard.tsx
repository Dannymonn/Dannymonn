import { useLiveQuery } from 'dexie-react-hooks'
import { useNavigate } from 'react-router-dom'
import { Dumbbell, Timer, TrendingUp, Flame } from 'lucide-react'
import { db } from '../db/database'

export default function Dashboard() {
  const navigate = useNavigate()
  const routines = useLiveQuery(() => db.routines.toArray(), [], [])
  const sessions = useLiveQuery(() => db.workoutSessions.orderBy('startTime').reverse().limit(5).toArray(), [], [])
  const totalWorkouts = useLiveQuery(() => db.workoutSessions.count(), [], 0)
  const totalVolume = useLiveQuery(async () => {
    const all = await db.workoutSessions.toArray()
    return all.reduce((s, w) => s + w.totalVolume, 0)
  }, [], 0)
  const totalDist = useLiveQuery(async () => {
    const all = await db.runSessions.toArray()
    return all.reduce((s, r) => s + r.distanceKm, 0)
  }, [], 0)
  const latestGoal = useLiveQuery(() => db.goals.orderBy('createdAt').last(), [], null)
  const latestWeight = useLiveQuery(() => db.weightEntries.orderBy('recordedAt').last(), [], null)

  return (
    <div className="page">
      <h1 className="text-2xl font-bold mb-4">Workout Tracker</h1>

      {/* Stats */}
      <div className="grid grid-cols-2 gap-3 mb-4">
        {[
          { label: 'Workouts', value: totalWorkouts, icon: Dumbbell },
          { label: 'Total Volume', value: `${Math.round(totalVolume ?? 0)} kg`, icon: TrendingUp },
          { label: 'KM Run', value: `${(totalDist ?? 0).toFixed(1)}`, icon: Timer },
          { label: 'Goal', value: latestGoal ? `${latestGoal.targetWeightKg} kg` : 'Not set', icon: Flame },
        ].map(({ label, value, icon: Icon }) => (
          <div key={label} className="card">
            <p className="text-text-muted text-xs mb-1 flex items-center gap-1"><Icon size={12} />{label}</p>
            <p className="text-accent text-xl font-bold">{value}</p>
          </div>
        ))}
      </div>

      {/* Weight */}
      {(latestGoal || latestWeight) && (
        <div className="card mb-4">
          {latestGoal && <p className="text-sm">Target: <span className="text-accent font-semibold">{latestGoal.targetWeightKg} kg</span></p>}
          {latestWeight && <p className="text-sm text-text-muted">Current: {latestWeight.weightKg} kg</p>}
        </div>
      )}

      {/* Quick start */}
      <h2 className="font-semibold text-lg mb-2">Quick Start</h2>
      <div className="flex gap-2 mb-4">
        <button className="btn-primary flex-1" onClick={() => navigate('/workout/0/Free%20Workout')}>
          Free Workout
        </button>
        <button className="btn-outline flex-1" onClick={() => navigate('/run')}>
          Start Run
        </button>
      </div>

      {/* Routines */}
      {(routines?.length ?? 0) > 0 && (
        <>
          <h2 className="font-semibold text-lg mb-2">My Routines</h2>
          <div className="flex flex-col gap-2 mb-4">
            {routines?.map(r => (
              <button key={r.id} onClick={() => navigate(`/workout/${r.id}/${encodeURIComponent(r.name)}`)}
                className="card text-left border border-accent/30 active:bg-white/5">
                <p className="font-semibold">{r.name}</p>
                <p className="text-text-muted text-sm">{r.description || 'Tap to start'}</p>
              </button>
            ))}
          </div>
        </>
      )}

      {/* Recent */}
      {(sessions?.length ?? 0) > 0 && (
        <>
          <h2 className="font-semibold text-lg mb-2">Recent Workouts</h2>
          <div className="flex flex-col gap-2">
            {sessions?.map(s => (
              <div key={s.id} className="card flex justify-between items-center">
                <div>
                  <p className="font-medium">{s.routineName}</p>
                  <p className="text-text-muted text-xs">{new Date(s.startTime).toLocaleDateString()}</p>
                </div>
                <p className="text-accent text-sm">{s.totalVolume > 0 ? `${Math.round(s.totalVolume)} kg` : ''}</p>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  )
}
