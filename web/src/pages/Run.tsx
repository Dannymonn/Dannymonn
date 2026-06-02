import { useState, useEffect, useRef } from 'react'
import { useLiveQuery } from 'dexie-react-hooks'
import { Play, Square, Heart } from 'lucide-react'
import { db } from '../db/database'

export default function RunPage() {
  const [running, setRunning] = useState(false)
  const [elapsed, setElapsed] = useState(0)
  const [distance, setDistance] = useState('')
  const [notes, setNotes] = useState('')
  const [hr, setHr] = useState<number | null>(null)
  const [hrMax, setHrMax] = useState(0)
  const hrList = useRef<number[]>([])
  const startRef = useRef(0)
  const bleCharRef = useRef<BluetoothRemoteGATTCharacteristic | null>(null)

  const recentRuns = useLiveQuery(() => db.runSessions.orderBy('startTime').reverse().limit(10).toArray(), [], [])

  useEffect(() => {
    if (!running) return
    const t = setInterval(() => setElapsed(Math.floor((Date.now() - startRef.current) / 1000)), 1000)
    return () => clearInterval(t)
  }, [running])

  const start = () => {
    startRef.current = Date.now()
    setElapsed(0); setHr(null); setHrMax(0)
    hrList.current = []
    setRunning(true)
  }

  const stop = async () => {
    setRunning(false)
    bleCharRef.current?.service?.device?.gatt?.disconnect()
    const dist = parseFloat(distance) || 0
    const avgHr = hrList.current.length ? Math.round(hrList.current.reduce((a, b) => a + b, 0) / hrList.current.length) : 0
    await db.runSessions.add({
      startTime: startRef.current, durationSeconds: elapsed,
      distanceKm: dist, avgHeartRate: avgHr, maxHeartRate: hrMax, notes
    })
    setDistance(''); setNotes('')
  }

  const connectHR = async () => {
    try {
      const device = await (navigator as any).bluetooth.requestDevice({
        filters: [{ services: ['heart_rate'] }]
      })
      const server = await device.gatt!.connect()
      const service = await server.getPrimaryService('heart_rate')
      const char = await service.getCharacteristic('heart_rate_measurement')
      bleCharRef.current = char
      await char.startNotifications()
      char.addEventListener('characteristicvaluechanged', (e: Event) => {
        const view = (e.target as BluetoothRemoteGATTCharacteristic).value!
        const flag = view.getUint8(0)
        const bpm = flag & 1 ? view.getUint16(1, true) : view.getUint8(1)
        setHr(bpm)
        hrList.current.push(bpm)
        setHrMax(prev => Math.max(prev, bpm))
      })
    } catch { /* user cancelled or not supported */ }
  }

  const mins = Math.floor(elapsed / 60), secs = elapsed % 60
  const pace = distance && parseFloat(distance) > 0
    ? (elapsed / 60 / parseFloat(distance)).toFixed(1) : '--'

  return (
    <div className="page">
      <h1 className="text-2xl font-bold mb-4">Running</h1>

      {/* Active panel */}
      <div className="card mb-4 text-center">
        <p className="text-6xl font-mono font-bold text-accent mb-1">
          {String(mins).padStart(2,'0')}:{String(secs).padStart(2,'0')}
        </p>
        {hr !== null && (
          <p className="text-accent-red flex items-center justify-center gap-1 text-xl font-bold">
            <Heart size={18} fill="currentColor" /> {hr} bpm
          </p>
        )}
        {pace !== '--' && <p className="text-text-muted text-sm mt-1">{pace} min/km</p>}
      </div>

      {running && (
        <div className="card mb-4 flex flex-col gap-3">
          <div>
            <label className="text-xs text-text-muted block mb-1">Distance (km)</label>
            <input type="number" inputMode="decimal" value={distance} onChange={e => setDistance(e.target.value)} placeholder="0.0" />
          </div>
          <div>
            <label className="text-xs text-text-muted block mb-1">Notes</label>
            <input value={notes} onChange={e => setNotes(e.target.value)} placeholder="Optional" />
          </div>
        </div>
      )}

      <div className="flex gap-2 mb-4">
        {!running ? (
          <button className="btn-primary flex-1 flex items-center justify-center gap-2" onClick={start}>
            <Play size={18} /> Start Run
          </button>
        ) : (
          <button className="btn-outline flex-1 flex items-center justify-center gap-2 border-accent-red text-accent-red" onClick={stop}>
            <Square size={18} /> Stop & Save
          </button>
        )}
        <button className="btn-ghost flex items-center gap-1" onClick={connectHR}>
          <Heart size={16} /> HR Sensor
        </button>
      </div>

      {/* Recent runs */}
      <h2 className="font-semibold text-lg mb-2">Recent Runs</h2>
      {recentRuns?.length === 0 && <p className="text-text-muted">No runs yet.</p>}
      {recentRuns?.map(r => {
        const m = Math.floor(r.durationSeconds / 60), s = r.durationSeconds % 60
        const p = r.distanceKm > 0 ? ((r.durationSeconds / 60) / r.distanceKm).toFixed(1) : null
        return (
          <div key={r.id} className="card mb-2 flex justify-between items-center">
            <div>
              <p className="text-text-muted text-xs">{new Date(r.startTime).toLocaleDateString()}</p>
              <p className="font-bold text-lg">{r.distanceKm.toFixed(2)} km</p>
              {r.avgHeartRate > 0 && <p className="text-accent-red text-xs">{r.avgHeartRate} bpm avg</p>}
            </div>
            <div className="text-right">
              <p className="text-accent font-mono">{String(m).padStart(2,'0')}:{String(s).padStart(2,'0')}</p>
              {p && <p className="text-text-muted text-xs">{p} min/km</p>}
            </div>
          </div>
        )
      })}
    </div>
  )
}
