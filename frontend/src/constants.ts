import type { Category, CertificateStatus, Level } from '@/types'

export const CATEGORY_LABELS: Record<Category, string> = {
  LANGUAGE: '语言能力', PROFESSIONAL: '职业资格', COMPETITION: '竞赛获奖',
  SKILL: '技能认证', ACADEMIC: '学术成果', HONOR: '荣誉表彰', OTHER: '其他证书',
}

export const LEVEL_LABELS: Record<Level, string> = {
  INTERNATIONAL: '国际级', NATIONAL: '国家级', PROVINCIAL: '省级',
  MUNICIPAL: '市级', UNIVERSITY: '校级', OTHER: '其他',
}

export const STATUS_LABELS: Record<CertificateStatus, string> = {
  VALID: '有效', EXPIRING: '即将到期', EXPIRED: '已到期', PERMANENT: '长期有效',
}

export const CATEGORY_OPTIONS = Object.entries(CATEGORY_LABELS) as [Category, string][]
export const LEVEL_OPTIONS = Object.entries(LEVEL_LABELS) as [Level, string][]

