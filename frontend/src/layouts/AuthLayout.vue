<script setup lang="ts">
import { Archive, BellRing, ShieldCheck } from 'lucide-vue-next'
import LogoMark from '@/components/LogoMark.vue'
</script>

<template>
  <main class="auth-shell">
    <section class="story-panel">
      <div class="story-content">
        <LogoMark light />
        <div class="folio">CAMPUS ARCHIVE · 2026</div>
        <h1 class="display">收藏每一份<br><em>成长的证明</em></h1>
        <p class="lede">从第一张语言证书到毕业前的专业认证，证途替你妥善归档，也及时提醒每一个重要期限。</p>
        <div class="feature-line">
          <div><Archive :size="18" /><span><b>统一归档</b><small>证书与附件集中管理</small></span></div>
          <div><BellRing :size="18" /><span><b>到期提醒</b><small>提前 90 天识别期限</small></span></div>
          <div><ShieldCheck :size="18" /><span><b>个人空间</b><small>数据隔离，安全存放</small></span></div>
        </div>
      </div>
      <div class="orbit orbit-a"/><div class="orbit orbit-b"/>
      <div class="seal"><span>证</span><small>PATH</small></div>
    </section>
    <section class="form-panel"><div class="form-wrap"><slot /></div></section>
  </main>
</template>

<style scoped>
.auth-shell { min-height: 100vh; display: grid; grid-template-columns: minmax(430px, 1.08fr) minmax(430px, .92fr); background: var(--paper); }
.story-panel { position: relative; overflow: hidden; padding: clamp(36px,5vw,72px); display: flex; background: var(--ink); color: var(--paper); }
.story-panel::before { content:''; position:absolute; inset:0; opacity:.2; background: linear-gradient(115deg, transparent 0 47%, rgba(255,255,255,.08) 47.2% 47.35%, transparent 47.5%), radial-gradient(circle at 20% 80%, rgba(217,164,65,.22), transparent 35%); }
.story-content { width: min(620px,100%); position:relative; z-index:2; display:flex; flex-direction:column; }
.folio { margin-top: auto; color: var(--gold); font-size: 10px; letter-spacing: .25em; font-weight: 700; }
h1 { margin: 22px 0 18px; font-size: clamp(48px,6vw,84px); line-height: .98; letter-spacing: -.035em; }
h1 em { color: var(--gold); font-style: italic; font-weight: 400; }
.lede { width: min(520px,90%); margin:0; color:rgba(255,255,255,.62); line-height:1.85; font-size:15px; }
.feature-line { margin-top: auto; padding-top: 48px; display: grid; grid-template-columns: repeat(3,1fr); border-top:1px solid rgba(255,255,255,.12); gap:18px; }
.feature-line>div { display:grid; grid-template-columns:24px 1fr; gap:8px; color:var(--gold); }.feature-line span b,.feature-line span small{display:block}.feature-line b{color:white;font-size:12px}.feature-line small{margin-top:5px;color:rgba(255,255,255,.4);font-size:9px;line-height:1.5}
.orbit { position:absolute; border:1px solid rgba(255,255,255,.08); border-radius:50%; }.orbit-a{width:560px;height:560px;right:-270px;top:-190px}.orbit-b{width:420px;height:420px;right:-200px;top:-120px}
.seal { position:absolute; right:clamp(38px,6vw,90px); top:clamp(120px,18vh,190px); width:100px;height:100px;border-radius:50%;display:flex;flex-direction:column;align-items:center;justify-content:center;border:1px solid rgba(201,80,61,.75);color:#e36a54;transform:rotate(9deg);opacity:.9; }
.seal::after{content:'';position:absolute;inset:7px;border:1px dashed currentColor;border-radius:50%}.seal span{font-size:36px;font-weight:700;line-height:1}.seal small{font-size:7px;letter-spacing:.3em;margin-top:5px}
.form-panel { display:grid;place-items:center;padding:40px; position:relative; }.form-panel::before{content:'';position:absolute;inset:0;background-image:linear-gradient(rgba(23,50,77,.03) 1px,transparent 1px),linear-gradient(90deg,rgba(23,50,77,.03) 1px,transparent 1px);background-size:28px 28px;mask-image:linear-gradient(to bottom right,black,transparent 72%)}
.form-wrap{position:relative;width:min(430px,100%)}
@media(max-width:880px){.auth-shell{grid-template-columns:1fr}.story-panel{min-height:290px;padding:28px 28px 34px}.folio{margin-top:60px}h1{font-size:43px;margin:12px 0}.lede{font-size:13px;line-height:1.65}.feature-line{display:none}.seal{right:30px;top:75px;width:72px;height:72px}.seal span{font-size:26px}.form-panel{padding:42px 22px 60px}}
</style>

