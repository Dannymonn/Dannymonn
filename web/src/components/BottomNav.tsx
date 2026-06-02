import { NavLink } from 'react-router-dom'
import { Home, Dumbbell, Timer, History, Target } from 'lucide-react'

const tabs = [
  { to: '/', icon: Home, label: 'Home' },
  { to: '/routines', icon: Dumbbell, label: 'Routines' },
  { to: '/run', icon: Timer, label: 'Run' },
  { to: '/history', icon: History, label: 'History' },
  { to: '/goals', icon: Target, label: 'Goals' },
]

export default function BottomNav() {
  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-surface border-t border-divider pb-safe z-50">
      <div className="flex max-w-600 mx-auto">
        {tabs.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/'}
            className={({ isActive }) =>
              `flex-1 flex flex-col items-center py-3 gap-0.5 text-xs transition-colors ${
                isActive ? 'text-accent' : 'text-text-muted'
              }`
            }
          >
            <Icon size={22} />
            <span>{label}</span>
          </NavLink>
        ))}
      </div>
    </nav>
  )
}
