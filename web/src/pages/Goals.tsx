import { useState, useRef } from 'react'
import { useLiveQuery } from 'dexie-react-hooks'
import { Camera, Target, Scale, Trash2 } from 'lucide-react'
import { db } from '../db/database'
import type { PhotoAngle } from '../types'

const ANGLES: PhotoAngle[] = ['FRONT', 'SIDE', 'BACK']

export default function Goals() {
  const [targetWeight, setTargetWeight] = useState('')
  const [currentWeight, setCurrentWeight] = useState('')
  const [logWeight, setLogWeight] = useState('')
  const [angle, setAngle] = useState<PhotoAngle>('FRONT')
  const fileRef = useRef<HTMLInputElement>(null)

  const latestGoal = useLiveQuery(() => db.goals.orderBy('createdAt').last(), [], null)
  const weightEntries = useLiveQuery(() => db.weightEntries.orderBy('recordedAt').reverse().toArray(), [], [])
  const photos = useLiveQuery(() => db.progressPhotos.orderBy('takenAt').reverse().toArray(), [], [])

  const saveGoal = async () => {
    const t = parseFloat(targetWeight), c = parseFloat(currentWeight)
    if (!t || !c) return
    await db.goals.add({ targetWeightKg: t, currentWeightKg: c, createdAt: Date.now() })
    setTargetWeight(''); setCurrentWeight('')
  }

  const addWeight = async () => {
    const w = parseFloat(logWeight)
    if (!w) return
    await db.weightEntries.add({ weightKg: w, recordedAt: Date.now(), notes: '' })
    setLogWeight('')
  }

  const takePhoto = () => fileRef.current?.click()

  const handlePhoto = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return
    const reader = new FileReader()
    reader.onload = async ev => {
      const dataUrl = ev.target?.result as string
      const latest = await db.weightEntries.orderBy('recordedAt').last()
      await db.progressPhotos.add({ dataUrl, angle, takenAt: Date.now(), weightAtTime: latest?.weightKg })
    }
    reader.readAsDataURL(file)
    e.target.value = ''
  }

  const latestW = weightEntries?.[0]?.weightKg
  const progress = latestGoal && latestW
    ? Math.min(100, Math.max(0, Math.abs((latestW - latestGoal.currentWeightKg) / (latestGoal.targetWeightKg - latestGoal.currentWeightKg)) * 100))
    : 0

  return (
    <div className="page">
      <h1 className="text-2xl font-bold mb-4">Goals & Progress</h1>

      {/* Goal card */}
      <div className="card mb-4">
        <div className="flex items-center gap-2 mb-3">
          <Target size={18} className="text-accent" />
          <h2 className="font-semibold">Weight Goal</h2>
        </div>
        {latestGoal ? (
          <>
            <div className="flex justify-between mb-1">
              <span className="text-text-muted text-sm">Current: <span className="text-text-primary">{latestW ?? latestGoal.currentWeightKg} kg</span></span>
              <span className="text-text-muted text-sm">Target: <span className="text-accent font-bold">{latestGoal.targetWeightKg} kg</span></span>
            </div>
            <div className="w-full h-2.5 bg-divider rounded-full overflow-hidden mb-1">
              <div className="h-full bg-accent transition-all" style={{ width: `${progress}%` }} />
            </div>
            <p className="text-text-muted text-xs text-right">{Math.round(progress)}% to goal</p>
          </>
        ) : (
          <p className="text-text-muted text-sm mb-3">No goal set yet</p>
        )}
        <div className="grid grid-cols-2 gap-2 mt-3">
          <div>
            <label className="text-xs text-text-muted block mb-1">Current (kg)</label>
            <input type="number" inputMode="decimal" value={currentWeight} onChange={e => setCurrentWeight(e.target.value)} />
          </div>
          <div>
            <label className="text-xs text-text-muted block mb-1">Target (kg)</label>
            <input type="number" inputMode="decimal" value={targetWeight} onChange={e => setTargetWeight(e.target.value)} />
          </div>
        </div>
        <button className="btn-primary w-full mt-3" onClick={saveGoal}>Set Goal</button>
      </div>

      {/* Log weight */}
      <div className="card mb-4">
        <div className="flex items-center gap-2 mb-3">
          <Scale size={18} className="text-accent" />
          <h2 className="font-semibold">Log Weight</h2>
        </div>
        <div className="flex gap-2">
          <input type="number" inputMode="decimal" value={logWeight} onChange={e => setLogWeight(e.target.value)}
            placeholder="e.g. 75.5" />
          <button className="btn-primary whitespace-nowrap" onClick={addWeight}>Log</button>
        </div>
        {(weightEntries?.length ?? 0) > 0 && (
          <div className="mt-3 flex flex-col gap-1 max-h-40 overflow-y-auto">
            {weightEntries?.slice(0, 8).map(e => (
              <div key={e.id} className="flex justify-between text-sm py-1 border-b border-divider">
                <span className="text-text-muted">{new Date(e.recordedAt).toLocaleDateString()}</span>
                <span className="font-semibold">{e.weightKg} kg</span>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Progress photos */}
      <div className="mb-4">
        <div className="flex justify-between items-center mb-2">
          <div className="flex items-center gap-2">
            <Camera size={18} className="text-accent" />
            <h2 className="font-semibold">Progress Photos</h2>
          </div>
          <div className="flex gap-2 items-center">
            <select className="w-24 py-1.5 text-sm" value={angle} onChange={e => setAngle(e.target.value as PhotoAngle)}>
              {ANGLES.map(a => <option key={a} value={a}>{a}</option>)}
            </select>
            <button className="btn-outline py-1.5 px-3 flex items-center gap-1 text-sm" onClick={takePhoto}>
              <Camera size={14} /> Photo
            </button>
            <input ref={fileRef} type="file" accept="image/*" capture="environment"
              className="hidden" onChange={handlePhoto} />
          </div>
        </div>
        {photos?.length === 0 && <p className="text-text-muted text-sm">No photos yet. Long-press to delete.</p>}
        <div className="grid grid-cols-2 gap-2">
          {photos?.map(p => (
            <div key={p.id} className="relative rounded-xl overflow-hidden">
              <img src={p.dataUrl} alt="progress" className="w-full h-40 object-cover" />
              <div className="absolute bottom-0 left-0 right-0 bg-black/60 px-2 py-1 flex justify-between items-center">
                <span className="text-xs text-accent font-bold">{p.angle}</span>
                <span className="text-xs text-white">{new Date(p.takenAt).toLocaleDateString()}</span>
                <button onClick={() => db.progressPhotos.delete(p.id!)}
                  className="text-white"><Trash2 size={12} /></button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
