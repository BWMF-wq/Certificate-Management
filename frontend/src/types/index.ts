export type Category = 'LANGUAGE' | 'PROFESSIONAL' | 'COMPETITION' | 'SKILL' | 'ACADEMIC' | 'HONOR' | 'OTHER'
export type Level = 'NATIONAL' | 'PROVINCIAL_MUNICIPAL' | 'DISTRICT_COUNTY' | 'UNIVERSITY' | 'OTHER'
export type AwardType = 'INDIVIDUAL' | 'TEAM' | 'UNIT'

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
  awardType: AwardType
  issueDate: string
  expiryDate?: string | null
  credentialNo?: string
  credentialUrl?: string
  description?: string
}

export interface Certificate extends CertificatePayload {
  id: number
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
  thisYear: number
  withAttachment: number
  issuerCount: number
  categories: { category: Category; count: number }[]
  levels: { level: Level; count: number }[]
  awardTypes: { awardType: AwardType; count: number }[]
  monthlyTrend: { month: string; count: number }[]
  recent: Certificate[]
}

