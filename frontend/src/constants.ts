import type { AwardType, Category, Level } from '@/types'

export const CATEGORY_LABELS: Record<Category, string> = {
  ACADEMIC_COMPETITION: '学科竞赛', INNOVATION_ENTREPRENEURSHIP: '创新创业',
  CULTURE_SPORTS: '文体活动', SOCIAL_PRACTICE: '社会实践',
  VOLUNTEER_SERVICE: '志愿服务', HONORARY_TITLE: '荣誉称号', OTHER: '其他荣誉',
}

export const LEVEL_LABELS: Record<Level, string> = {
  NATIONAL: '国家级荣誉', PROVINCIAL_MUNICIPAL: '省市级荣誉',
  DISTRICT_COUNTY: '区县级荣誉', UNIVERSITY: '校级荣誉', OTHER: '其他荣誉',
}

export const AWARD_TYPE_LABELS: Record<AwardType, string> = {
  INDIVIDUAL: '个人奖', TEAM: '团体奖', UNIT: '单位奖',
}

export const CATEGORY_OPTIONS = Object.entries(CATEGORY_LABELS) as [Category, string][]
export const LEVEL_OPTIONS = Object.entries(LEVEL_LABELS) as [Level, string][]
export const AWARD_TYPE_OPTIONS = Object.entries(AWARD_TYPE_LABELS) as [AwardType, string][]

