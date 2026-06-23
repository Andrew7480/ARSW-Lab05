import { useEffect, useRef, useState } from 'react'
import { createStompClient, subscribeBlueprint } from './lib/stompClient.js'
import { createSocket } from './lib/socketIoClient.js'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'
const IO_BASE  = import.meta.env.VITE_IO_BASE  ?? 'http://localhost:3001'

export default function App() {
  // ── Estado ──────────────────────────────────────────────────────────────────
  const [tech, setTech]                   = useState('stomp')
  const [author, setAuthor]               = useState('juan')
  const [name, setName]                   = useState('')           // plano activo
  const [blueprintsList, setBlueprintsList] = useState([])         // tabla del autor
  const [points, setPoints]               = useState([])           // puntos del canvas

  const canvasRef  = useRef(null)
  const stompRef   = useRef(null)
  const unsubRef   = useRef(null)
  const socketRef  = useRef(null)

  // ── Helpers ─────────────────────────────────────────────────────────────────

  function drawCanvas(pts) {
    const ctx = canvasRef.current?.getContext('2d')
    if (!ctx) return
    ctx.clearRect(0, 0, 600, 400)
    ctx.beginPath()
    pts.forEach((p, i) => {
      if (i === 0) ctx.moveTo(p.x, p.y)
      else ctx.lineTo(p.x, p.y)
    })
    ctx.stroke()
  }

  // Recarga la tabla del autor actual
  async function loadList() {
    if (!author) return
    try {
      const res = await fetch(`${API_BASE}/api/blueprints/${author}`).then(r => r.json())
      setBlueprintsList(Array.from(res.data ?? []))
    } catch {
      setBlueprintsList([])
    }
  }

  // ── Efecto 1: lista de planos cuando cambia el autor ────────────────────────
  useEffect(() => {
    setBlueprintsList([])
    setName('')
    setPoints([])
    loadList()
  }, [author, tech])

  // ── Efecto 2: carga el plano seleccionado en el canvas ──────────────────────
  useEffect(() => {
    if (!name) { setPoints([]); return }
    async function load() {
      const res = await fetch(`${API_BASE}/api/blueprints/${author}/${name}`).then(r => r.json())
      setPoints(res.data?.points ?? [])
    }
    load()
  }, [author, name, tech])

  // ── Efecto 3: redibuja el canvas cada vez que cambian los puntos ─────────────
  useEffect(() => {
    drawCanvas(points)
  }, [points])

  // ── Efecto 4: conexión RT (STOMP o Socket.IO) ────────────────────────────────
  useEffect(() => {
    // Limpia conexión anterior
    unsubRef.current?.()
    unsubRef.current = null
    stompRef.current?.deactivate?.()
    stompRef.current = null
    socketRef.current?.disconnect?.()
    socketRef.current = null

    if (!name) return   // sin plano activo, no hay nada que escuchar

    if (tech === 'stomp') {
      const client = createStompClient(API_BASE)
      stompRef.current = client
      client.onConnect = () => {
        unsubRef.current = subscribeBlueprint(client, author, name, (upd) => {
          // Agrega los puntos que llegan por RT al estado (el Efecto 3 redibuja)
          setPoints(prev => [...prev, ...upd.points])
        })
      }
      client.activate()
    } else {
      const s = createSocket(IO_BASE)
      socketRef.current = s
      s.emit('join-room', `blueprints.${author}.${name}`)
      s.on('blueprint-update', (upd) => {
        setPoints(prev => [...prev, ...upd.points])
      })
    }

    return () => {
      unsubRef.current?.()
      stompRef.current?.deactivate?.()
      socketRef.current?.disconnect?.()
    }
  }, [tech, author, name])

  // ── Click en canvas ──────────────────────────────────────────────────────────
  function onCanvasClick(e) {
    if (!name) return
    const rect  = e.target.getBoundingClientRect()
    const point = { x: Math.round(e.clientX - rect.left), y: Math.round(e.clientY - rect.top) }

    // Agrega el punto localmente (el Efecto 3 redibuja)
    setPoints(prev => [...prev, point])

    // Publica por RT para que las otras pestañas lo reciban
    if (tech === 'stomp' && stompRef.current?.connected) {
      stompRef.current.publish({
        destination: '/app/draw',
        body: JSON.stringify({ author, name, point })
      })
    } else if (tech === 'socketio' && socketRef.current?.connected) {
      socketRef.current.emit('draw-event', {
        room: `blueprints.${author}.${name}`,
        author, name, point
      })
    }
  }

  // ── CRUD ────────────────────────────────────────────────────────────────────

  async function handleCreate() {
    const newName = prompt('Nombre del nuevo plano:')
    if (!newName?.trim()) return
    await fetch(`${API_BASE}/api/blueprints`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ author, name: newName.trim(), points: [] })
    })
    setName(newName.trim())
    setPoints([])
    loadList()
  }

  async function handleSave() {
    if (!name) return
    await fetch(`${API_BASE}/api/blueprints/${author}/${name}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(points)
    })
    loadList()
  }

  async function handleDelete() {
    if (!name) return
    await fetch(`${API_BASE}/api/blueprints/${author}/${name}`, { method: 'DELETE' })
    setName('')
    setPoints([])
    loadList()
  }

  // Total de puntos del autor (reduce sobre la lista)
  const totalPoints = blueprintsList.reduce((sum, bp) => sum + (bp.points?.length ?? 0), 0)

  // ── UI ──────────────────────────────────────────────────────────────────────
  return (
    <div style={{ fontFamily: 'Inter, system-ui', padding: 16, maxWidth: 960 }}>
      <h2 style={{ marginBottom: 12 }}>BluePrints RT – Socket.IO vs STOMP</h2>

      {/* Barra de controles */}
      <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 16 }}>
        <label>Tecnología:</label>
        <select value={tech} onChange={e => setTech(e.target.value)}>
          <option value="stomp">STOMP (Spring)</option>
          <option value="socketio">Socket.IO (Node)</option>
        </select>
        <label>Autor:</label>
        <input
          value={author}
          onChange={e => setAuthor(e.target.value)}
          placeholder="autor"
          style={{ width: 100 }}
        />
      </div>

      <div style={{ display: 'flex', gap: 24, alignItems: 'flex-start' }}>

        {/* Panel izquierdo: tabla de planos */}
        <div style={{ minWidth: 230 }}>
          <div style={{ display: 'flex', gap: 4, marginBottom: 8 }}>
            <button onClick={handleCreate}>+ Crear</button>
            <button onClick={handleSave}  disabled={!name}>Guardar</button>
            <button onClick={handleDelete} disabled={!name} style={{ color: 'crimson' }}>Eliminar</button>
          </div>

          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 13 }}>
            <thead>
              <tr>
                <th style={th}>Plano</th>
                <th style={th}>Puntos</th>
              </tr>
            </thead>
            <tbody>
              {blueprintsList.length === 0 && (
                <tr><td colSpan={2} style={{ ...td, opacity: .5 }}>Sin planos</td></tr>
              )}
              {blueprintsList.map(bp => (
                <tr
                  key={bp.name}
                  onClick={() => setName(bp.name)}
                  style={{
                    cursor: 'pointer',
                    background: bp.name === name ? '#dbeafe' : 'transparent'
                  }}
                >
                  <td style={td}>{bp.name}</td>
                  <td style={td}>{bp.points?.length ?? 0}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td style={{ ...td, fontWeight: 700 }}>Total</td>
                <td style={{ ...td, fontWeight: 700 }}>{totalPoints}</td>
              </tr>
            </tfoot>
          </table>
        </div>

        {/* Panel derecho: canvas */}
        <div>
          <p style={{ margin: '0 0 6px', fontSize: 13, opacity: .7 }}>
            {name
              ? `Editando: ${author} / ${name} — ${points.length} puntos`
              : 'Selecciona un plano de la tabla para empezar'}
          </p>
          <canvas
            ref={canvasRef}
            width={600}
            height={400}
            style={{
              border: '1px solid #ddd',
              borderRadius: 12,
              cursor: name ? 'crosshair' : 'not-allowed'
            }}
            onClick={onCanvasClick}
          />
          <p style={{ fontSize: 12, opacity: .6, marginTop: 6 }}>
            Abre 2 pestañas con el mismo autor/plano y dibuja en ambas para ver la colaboración en tiempo real.
          </p>
        </div>
      </div>
    </div>
  )
}

// Estilos de tabla
const th = { textAlign: 'left', padding: '4px 8px', borderBottom: '1px solid #ddd', background: '#f5f5f5' }
const td = { padding: '4px 8px', borderBottom: '1px solid #eee' }
