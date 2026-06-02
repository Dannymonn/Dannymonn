import { Routes, Route, Navigate } from 'react-router-dom'
import BottomNav from './components/BottomNav'
import Dashboard from './pages/Dashboard'
import Routines from './pages/Routines'
import EditRoutine from './pages/EditRoutine'
import ActiveWorkout from './pages/ActiveWorkout'
import ExerciseLibrary from './pages/ExerciseLibrary'
import RunPage from './pages/Run'
import Goals from './pages/Goals'
import History from './pages/History'
import WorkoutDetail from './pages/WorkoutDetail'

export default function App() {
  return (
    <div className="flex flex-col h-full bg-bg">
      <main className="flex-1 overflow-y-auto">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/routines" element={<Routines />} />
          <Route path="/routines/edit/:id" element={<EditRoutine />} />
          <Route path="/workout/:routineId/:routineName" element={<ActiveWorkout />} />
          <Route path="/exercises" element={<ExerciseLibrary />} />
          <Route path="/run" element={<RunPage />} />
          <Route path="/goals" element={<Goals />} />
          <Route path="/history" element={<History />} />
          <Route path="/history/:id" element={<WorkoutDetail />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <BottomNav />
    </div>
  )
}
