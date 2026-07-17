import type { AwardType, Category, Level } from '@/types'

export const CATEGORY_LABELS: Record<Category, string> = {
  ACADEMIC_EDUCATION: '学历学位', LANGUAGE_EXAM: '语言考试',
  PROFESSIONAL_QUALIFICATION: '职业资格', SKILL_CERTIFICATION: '技能认证',
  COMPETITION_AWARD: '竞赛获奖', INNOVATION_ENTREPRENEURSHIP: '创新创业',
  TRAINING_CERTIFICATE: '培训结业', HONORARY_TITLE: '荣誉称号',
  CULTURE_SPORTS: '文体活动', SOCIAL_PRACTICE: '社会实践',
  VOLUNTEER_SERVICE: '志愿服务', INTELLECTUAL_PROPERTY: '知识产权', OTHER: '其他证书',
}

export const LEVEL_LABELS: Record<Level, string> = {
  INTERNATIONAL: '国际级', NATIONAL: '国家级', PROVINCIAL: '省级', MUNICIPAL: '市级',
  DISTRICT_COUNTY: '区县级', UNIVERSITY: '校级', INDUSTRY: '行业级', INSTITUTION: '机构级', OTHER: '其他级别',
}

export const AWARD_TYPE_LABELS: Record<AwardType, string> = {
  INDIVIDUAL: '个人', TEAM: '团体', UNIT: '单位',
}

export const CATEGORY_OPTIONS = Object.entries(CATEGORY_LABELS) as [Category, string][]
export const LEVEL_OPTIONS = Object.entries(LEVEL_LABELS) as [Level, string][]
export const AWARD_TYPE_OPTIONS = Object.entries(AWARD_TYPE_LABELS) as [AwardType, string][]

