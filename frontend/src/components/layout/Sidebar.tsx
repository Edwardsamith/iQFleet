import { NavLink, useNavigate } from 'react-router-dom'
import {
  LayoutDashboard, Truck, Users, FileText, DollarSign,
  Settings, ChevronLeft, Menu, X, LogOut, UserCog,
} from 'lucide-react'
import { cn } from '@/lib/utils'
import { useAuth } from '@/context/AuthContext'

interface NavItem {
  label: string
  path: string
  icon: React.ReactNode
  badge?: number
}

const navItems: NavItem[] = [
  { label: 'Dashboard',   path: '/dashboard', icon: <LayoutDashboard size={17} /> },
  { label: 'Vehículos',   path: '/vehicles',  icon: <Truck size={17} /> },
  { label: 'Conductores', path: '/drivers',   icon: <Users size={17} /> },
  { label: 'Documentos',  path: '/documents', icon: <FileText size={17} /> },
  { label: 'Finanzas',    path: '/finances',  icon: <DollarSign size={17} /> },
  { label: 'Usuarios',    path: '/users',     icon: <UserCog size={17} /> },
]

const bottomItems: NavItem[] = [
  { label: 'Configuración', path: '/settings', icon: <Settings size={17} /> },
]

interface SidebarProps {
  collapsed: boolean
  onToggle: () => void
}

const ROLE_LABELS: Record<string, string> = {
  ROLE_OWNER: 'Propietario',
  ROLE_ADMIN: 'Administrador',
}

export function Sidebar({ collapsed, onToggle }: SidebarProps) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login', { replace: true })
  }

  const initials = user
    ? `${user.firstName?.[0] ?? ''}${user.lastName?.[0] ?? ''}`.toUpperCase()
    : 'U'
  const displayName = user ? `${user.firstName} ${user.lastName}` : 'Usuario'
  const roleLabel = user ? (ROLE_LABELS[user.role] ?? user.role) : ''

  return (
    <>
      {/* Mobile overlay */}
      {!collapsed && (
        <div
          className="fixed inset-0 z-20 bg-blue-950/40 backdrop-blur-sm lg:hidden"
          onClick={onToggle}
        />
      )}

      <aside
        className={cn(
          'fixed top-0 left-0 z-30 flex flex-col h-full',
          'bg-slate-950 text-white',
          'transition-[width] duration-slow ease-in-out',
          collapsed ? 'w-[64px]' : 'w-60',
          'lg:relative lg:z-auto',
        )}
      >
        {/* Logo */}
        <div className="flex items-center h-[60px] px-3.5 shrink-0">
          {/* Logo icon */}
          <div className="w-8 h-8 rounded-xl bg-blue-600 flex items-center justify-center shrink-0 font-bold text-sm shadow-inner-top">
            iQ
          </div>

          {!collapsed && (
            <span className="ml-3 font-bold text-base tracking-tight text-white whitespace-nowrap">
              Fleet
            </span>
          )}

          {/* Collapse toggle (desktop) */}
          <button
            onClick={onToggle}
            className={cn(
              'p-1.5 rounded-lg text-slate-500 hover:text-white hover:bg-white/5 transition-all duration-normal',
              'hidden lg:flex',
              collapsed ? 'mx-auto mt-0' : 'ml-auto',
            )}
            aria-label={collapsed ? 'Expandir' : 'Colapsar'}
          >
            <ChevronLeft
              size={15}
              className={cn('transition-transform duration-normal', collapsed && 'rotate-180')}
            />
          </button>

          {/* Mobile close */}
          <button
            onClick={onToggle}
            className="ml-auto p-1.5 rounded-lg text-slate-500 hover:text-white hover:bg-white/5 transition-all duration-fast lg:hidden"
          >
            <X size={15} />
          </button>
        </div>

        {/* Divider */}
        <div className="mx-3.5 h-px bg-white/5" />

        {/* Nav */}
        <nav className="flex-1 overflow-y-auto py-3 px-2 flex flex-col gap-0.5">
          {navItems.map((item) => (
            <SidebarLink key={item.path} item={item} collapsed={collapsed} />
          ))}
        </nav>

        {/* Divider */}
        <div className="mx-3.5 h-px bg-white/5" />

        {/* Bottom */}
        <div className="py-3 px-2 flex flex-col gap-0.5">
          {bottomItems.map((item) => (
            <SidebarLink key={item.path} item={item} collapsed={collapsed} />
          ))}
        </div>

        {/* User pill + logout */}
        <div className="mx-2 mb-3 flex flex-col gap-1">
          {!collapsed && (
            <div className="p-2.5 rounded-xl bg-white/5 flex items-center gap-2.5">
              <div className="w-7 h-7 rounded-lg bg-blue-600 flex items-center justify-center text-xs font-bold shrink-0">
                {initials}
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-xs font-medium text-white truncate">{displayName}</p>
                <p className="text-xs text-slate-500 truncate">{roleLabel}</p>
              </div>
            </div>
          )}

          <button
            onClick={handleLogout}
            title="Cerrar sesión"
            className={cn(
              'flex items-center gap-2.5 px-2.5 py-2 rounded-xl text-sm font-medium',
              'text-slate-500 hover:text-white hover:bg-white/5 transition-all duration-fast',
              collapsed && 'justify-center',
            )}
          >
            <LogOut size={16} className="shrink-0" />
            {!collapsed && <span>Cerrar sesión</span>}
          </button>
        </div>
      </aside>
    </>
  )
}

function SidebarLink({ item, collapsed }: { item: NavItem; collapsed: boolean }) {
  return (
    <NavLink
      to={item.path}
      title={collapsed ? item.label : undefined}
      className={({ isActive }) =>
        cn(
          'relative flex items-center gap-2.5 px-2.5 py-2 rounded-xl text-sm font-medium',
          'transition-all duration-fast',
          collapsed && 'justify-center',
          isActive
            ? 'bg-blue-600 text-white shadow-sm shadow-blue-900/40'
            : 'text-slate-500 hover:text-white hover:bg-white/5',
        )
      }
    >
      <span className="shrink-0">{item.icon}</span>
      {!collapsed && <span className="whitespace-nowrap">{item.label}</span>}
      {!collapsed && item.badge !== undefined && item.badge > 0 && (
        <span className="ml-auto bg-err text-white text-xs font-bold px-1.5 py-0.5 rounded-full min-w-[1.25rem] text-center">
          {item.badge > 99 ? '99+' : item.badge}
        </span>
      )}
    </NavLink>
  )
}

export function MobileMenuButton({ onClick }: { onClick: () => void }) {
  return (
    <button
      onClick={onClick}
      className="p-2 rounded-lg text-slate-400 hover:bg-blue-50 hover:text-blue-600 transition-all duration-fast lg:hidden"
      aria-label="Abrir menú"
    >
      <Menu size={18} />
    </button>
  )
}
