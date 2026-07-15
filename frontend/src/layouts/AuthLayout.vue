<script setup lang="ts">
import { BookOpen, CloudSun, Coffee, Footprints, MapPin } from 'lucide-vue-next'
</script>

<template>
  <main class="auth-shell">
    <section class="story-panel">
      <div class="story-content">
        <div class="day-meta">
          <span>10 月 16 日 · 星期三</span>
          <span><CloudSun :size="15" /> 18°C，多云</span>
        </div>

        <div class="day-copy">
          <p class="eyebrow">校园生活 · 今日便笺</p>
          <h1 class="display">今天的校园<br><em>从早八开始</em></h1>
          <p class="lede">下课后去图书馆还书，傍晚在操场跑两圈。别忘了周五前交奖学金材料。</p>
        </div>

        <div class="day-board" aria-label="今日校园安排">
          <div class="schedule-row">
            <time>08:00</time>
            <span class="schedule-icon"><BookOpen :size="17" /></span>
            <span><b>高等数学</b><small><MapPin :size="12" /> 东区教学楼 204</small></span>
          </div>
          <div class="schedule-row">
            <time>14:30</time>
            <span class="schedule-icon"><Coffee :size="17" /></span>
            <span><b>图书馆自习</b><small><MapPin :size="12" /> 三楼靠窗座位</small></span>
          </div>
          <div class="schedule-row">
            <time>19:00</time>
            <span class="schedule-icon"><Footprints :size="17" /></span>
            <span><b>和室友去夜跑</b><small><MapPin :size="12" /> 西操场集合</small></span>
          </div>
        </div>
      </div>
      <div class="campus-lines" aria-hidden="true"><i/><i/><i/><i/><i/></div>
    </section>
    <section class="form-panel"><div class="form-wrap"><slot /></div></section>
  </main>
</template>

<style scoped>
.auth-shell { min-height: 100vh; display: grid; grid-template-columns: minmax(430px, 1.08fr) minmax(430px, .92fr); background: var(--paper); }
.story-panel { position: relative; overflow: hidden; padding: clamp(36px,5vw,72px); display: flex; background: #17324d; color: var(--paper); }
.story-panel::before { content:''; position:absolute; inset:0; opacity:.26; background: radial-gradient(circle at 12% 12%, rgba(71,139,145,.6), transparent 32%), linear-gradient(155deg, transparent 0 64%, rgba(217,164,65,.2) 64.2%, transparent 64.5%); }
.story-content { width: min(590px,100%); position:relative; z-index:2; display:flex; flex-direction:column; justify-content:space-between; gap:clamp(34px,6vh,72px); }
.day-meta { display:flex; justify-content:space-between; align-items:center; gap:18px; padding-bottom:16px; border-bottom:1px solid rgba(255,255,255,.15); color:rgba(255,255,255,.7); font-size:11px; letter-spacing:.08em; }
.day-meta span:last-child { display:flex; align-items:center; gap:7px; }
.day-copy { margin:auto 0; }
.eyebrow { margin:0; color:#e4be69; font-size:10px; letter-spacing:.22em; font-weight:700; }
h1 { margin:18px 0 20px; font-size:clamp(48px,5.7vw,78px); line-height:1.05; letter-spacing:-.035em; }
h1 em { color:#e4be69; font-style:normal; font-weight:400; }
.lede { width:min(480px,92%); margin:0; color:rgba(255,255,255,.68); line-height:1.9; font-size:14px; }
.day-board { width:min(520px,100%); padding:6px 20px; border:1px solid rgba(255,255,255,.15); border-radius:14px; background:rgba(7,27,45,.24); backdrop-filter:blur(8px); }
.schedule-row { display:grid; grid-template-columns:54px 34px 1fr; gap:10px; align-items:center; min-height:72px; border-bottom:1px dashed rgba(255,255,255,.13); }
.schedule-row:last-child { border-bottom:0; }
.schedule-row time { color:#e4be69; font-family:'DM Serif Display',serif; font-size:15px; }
.schedule-icon { width:30px; height:30px; display:grid; place-items:center; border-radius:50%; color:#f6f0e1; background:rgba(255,255,255,.1); }
.schedule-row b,.schedule-row small { display:block; }
.schedule-row b { color:#fffdf8; font-size:12px; font-weight:600; }
.schedule-row small { margin-top:5px; display:flex; align-items:center; gap:4px; color:rgba(255,255,255,.44); font-size:9px; }
.campus-lines { position:absolute; right:-45px; bottom:-1px; width:42%; height:27%; opacity:.13; border:1px solid white; border-bottom:0; transform:skewX(-8deg); }
.campus-lines::before { content:''; position:absolute; left:-18%; right:-4%; top:-31%; height:32%; border:1px solid white; transform:skewX(35deg); }
.campus-lines i { position:relative; display:inline-block; width:17%; height:100%; margin-right:2%; border-left:1px solid white; border-right:1px solid white; }
.campus-lines i::after { content:''; position:absolute; left:18%; right:18%; top:22%; height:43%; border:1px solid white; }
.form-panel { display:grid;place-items:center;padding:40px; position:relative; }.form-panel::before{content:'';position:absolute;inset:0;background-image:linear-gradient(rgba(23,50,77,.03) 1px,transparent 1px),linear-gradient(90deg,rgba(23,50,77,.03) 1px,transparent 1px);background-size:28px 28px;mask-image:linear-gradient(to bottom right,black,transparent 72%)}
.form-wrap{position:relative;width:min(430px,100%)}
@media(max-width:880px){.auth-shell{grid-template-columns:1fr}.story-panel{min-height:330px;padding:25px 28px 30px}.story-content{gap:22px}.day-copy{margin:0}.day-meta{padding-bottom:12px}.day-meta span:last-child{display:none}h1{font-size:42px;margin:11px 0 12px}.lede{font-size:12px;line-height:1.7}.day-board{display:none}.campus-lines{width:46%;height:25%}.form-panel{padding:42px 22px 60px}}
</style>

