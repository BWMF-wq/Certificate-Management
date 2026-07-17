import type { AwardType, Category, Level } from '@/types'

export const CATEGORY_LABELS: Record<Category, string> = {
  LANGUAGE: '语言能力', PROFESSIONAL: '职业资格', COMPETITION: '竞赛获奖',
  SKILL: '技能认证', ACADEMIC: '学术成果', HONOR: '荣誉表彰', OTHER: '其他证书',
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

