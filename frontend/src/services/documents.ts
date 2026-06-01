import { http } from '@/lib/http'
import type { Document, DocumentType, DocumentStatus } from '@/types/domain'

export interface CreateDocumentPayload {
  name: string
  documentType: DocumentType
  referenceNumber?: string
  issuingEntity?: string
  issueDate?: string
  expiryDate?: string
  fileUrl?: string
  fileFormat?: string
  notes?: string
  driverId?: string
  vehicleId?: string
  uploadedBy: string
}

export interface RenewDocumentPayload {
  newExpiryDate: string
  newFileUrl?: string
  newFileFormat?: string
  replacedBy: string
}

export async function listDocuments(filters?: {
  driverId?: string
  vehicleId?: string
  type?: DocumentType
  status?: DocumentStatus
}): Promise<Document[]> {
  const params = new URLSearchParams()
  if (filters?.driverId) params.set('driverId', filters.driverId)
  if (filters?.vehicleId) params.set('vehicleId', filters.vehicleId)
  if (filters?.type) params.set('type', filters.type)
  if (filters?.status) params.set('status', filters.status)
  const query = params.toString()
  const response = await http.get<Document[]>(`/documents${query ? `?${query}` : ''}`)
  return response.data
}

export async function getDocumentAlerts(alertDays?: number): Promise<Document[]> {
  const query = alertDays != null ? `?alertDays=${alertDays}` : ''
  const response = await http.get<Document[]>(`/documents/alerts${query}`)
  return response.data
}

export async function createDocument(payload: CreateDocumentPayload): Promise<void> {
  await http.post('/documents', payload)
}

export async function renewDocument(id: string, payload: RenewDocumentPayload): Promise<void> {
  await http.put(`/documents/${id}/renew`, payload)
}

export async function deactivateDocument(id: string): Promise<void> {
  await http.patch(`/documents/${id}/deactivate`)
}

