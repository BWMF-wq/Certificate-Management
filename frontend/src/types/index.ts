export type Category = 'LANGUAGE' | 'PROFESSIONAL' | 'COMPETITION' | 'SKILL' | 'ACADEMIC' | 'HONOR' | 'OTHER'
export type Level = 'INTERNATIONAL' | 'NATIONAL' | 'PROVINCIAL' | 'MUNICIPAL' | 'UNIVERSITY' | 'OTHER'
export type CertificateStatus = 'VALID' | 'EXPIRING' | 'EXPIRED' | 'PERMANENT'

export interface User {
  id: number
  name: string
  studentId: string
  email: string
  school?: string
  major?: string
  graduationYear?: number
  createdAt: string
}

export interface AuthResponse { token: string; user: User }

export interface CertificatePayload {
  name: string
  issuer: string
  category: Category
  level: Level
  issueDate: string
  expiryDate?: string | null
  credentialNo?: string
  credentialUrl?: string
  description?: string
}

export interface Certificate extends CertificatePayload {
  id: number
  status: CertificateStatus
  fileName?: string
  fileContentType?: string
  fileSize?: number
  hasAttachment: boolean
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface DashboardData {
  total: number
  permanent: number
  valid: number
  expiring: number
  expired: number
  categories: { category: Category; count: number }[]
  monthlyTrend: { month: string; count: number }[]
  recent: Certificate[]
}

