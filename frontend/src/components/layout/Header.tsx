import { Bell, Search } from 'lucide-react'
import { MobileMenuButton } from './Sidebar'
import { useAuth } from '@/context/AuthContext'

interface HeaderProps {
  onMenuClick: () => void
  pageTitle?: string
  actions?: React.ReactNode
}

export function Header({ onMenuClick, pageTitle, actions }: HeaderProps) {
  const { user } = useAuth()

  const initials = user
    ? `${user.firstName?.[0] ?? ''}${user.lastName?.[0] ?? ''}`.toUpperCase()
    : 'U'

  return (
    <header className="h-[60px] bg-white border-b border-blue-100 flex items-center gap-3 px-4 shrink-0">
      <MobileMenuButton onClick={onMenuClick} />

      {pageTitle && (
        <h1 className="text-sm font-semibold text-slate-700 hidden sm:block">{pageTitle}</h1>
      )}

      {/* Search */}
      <div className="flex-1 max-w-xs hidden md:block">
        <div className="relative">
          <Search
            size={13}
            className="absolute inset-y-0 left-3 my-auto text-blue-300 pointer-events-none"
          />
          <input
            type="search"
            placeholder="Buscar..."
            className="w-full pl-8 pr-3 h-8 rounded-lg border border-blue-100 bg-blue-50/50 text-xs text-slate-600 placeholder:text-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-300 transition-all duration-normal"
          />
        </div>
      </div>

      <div className="ml-auto flex items-center gap-1.5">
        {actions}

        {/* Notifications */}
        <button className="relative p-2 rounded-lg text-slate-400 hover:bg-blue-50 hover:text-blue-600 transition-all duration-fast">
          <Bell size={16} />
          <span className="absolute top-1.5 right-1.5 w-1.5 h-1.5 bg-err rounded-full" />
        </button>

        {/* Divider */}
        <div className="w-px h-5 bg-blue-100 mx-1" />

        {/* Avatar */}
        <div
          className="w-8 h-8 rounded-xl bg-blue-600 flex items-center justify-center font-bold text-white text-xs"
          title={user ? `${user.firstName} ${user.lastName}` : 'Usuario'}
        >
          {initials}
        </div>
      </div>
    </header>
  )
}
