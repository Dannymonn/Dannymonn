import { useEffect, useState, useCallback } from 'react'
import { X } from 'lucide-react'

interface Props { seconds: number; onDone: () => void }

export default function RestTimer({ seconds, onDone }: Props) {
  const [remaining, setRemaining] = useState(seconds)

  const vibrate = useCallback(() => {
    if ('vibrate' in navigator) navigator.vibrate([400, 200, 400])
  }, [])

  useEffect(() => {
    setRemaining(seconds)
  }, [seconds])

  useEffect(() => {
    if (remaining <= 0) { vibrate(); onDone(); return }
    const t = setTimeout(() => setRemaining(r => r - 1), 1000)
    return () => clearTimeout(t)
  }, [remaining, onDone, vibrate])

  const pct = ((seconds - remaining) / seconds) * 100

  return (
    <div className="fixed inset-0 bg-black/80 z-50 flex items-center justify-center p-6">
      <div className="card w-full max-w-sm text-center relative">
        <button onClick={onDone} className="absolute top-3 right-3 text-text-muted"><X size={20} /></button>
        <p className="text-text-muted text-sm mb-2">Rest Timer</p>
        <p className="text-6xl font-bold text-accent mb-4">{remaining}s</p>
        <div className="w-full h-2 bg-divider rounded-full overflow-hidden">
          <div className="h-full bg-accent transition-all duration-1000" style={{ width: `${pct}%` }} />
        </div>
        <p className="text-text-muted text-sm mt-3">Tap × to skip</p>
      </div>
    </div>
  )
}
