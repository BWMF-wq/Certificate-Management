import os
import time
from pathlib import Path
from playwright.sync_api import sync_playwright


ROOT = Path(__file__).resolve().parent
ARTIFACTS = ROOT / "artifacts"
ARTIFACTS.mkdir(exist_ok=True)
BASE_URL = os.getenv("E2E_BASE_URL", "http://127.0.0.1:5174").rstrip("/")
RUN_ID = os.getenv("E2E_RUN_ID", str(int(time.time())))
STUDENT_ID = f"26{RUN_ID[-12:]}"
EMAIL = f"e2e-{RUN_ID}@example.edu.cn"


def field(container, label: str):
    return container.locator(".field").filter(has_text=label).locator("input, textarea, select").first


with sync_playwright() as playwright:
    browser = playwright.chromium.launch(headless=True)
    page = browser.new_page(viewport={"width": 1440, "height": 1000}, device_scale_factor=1)
    console_errors: list[str] = []
    page.on("console", lambda message: console_errors.append(message.text) if message.type == "error" else None)

    page.goto(f"{BASE_URL}/register")
    page.wait_for_load_state("networkidle")
    page.screenshot(path=ARTIFACTS / "01-register.png", full_page=True)

    register_form = page.locator("form")
    field(register_form, "姓名").fill("林知夏")
    field(register_form, "学号").fill(STUDENT_ID)
    field(register_form, "校园邮箱").fill(EMAIL)
    field(register_form, "设置密码").fill("Campus2026")
    field(register_form, "确认密码").fill("Campus2026")
    page.get_by_role("button", name="创建档案").click()
    page.wait_for_url("**/dashboard")
    page.wait_for_load_state("networkidle")
    page.get_by_role("heading", name="荣誉数据概况").wait_for()
    page.screenshot(path=ARTIFACTS / "02-dashboard-empty.png", full_page=True)

    page.get_by_role("button", name="退出登录").click()
    page.wait_for_url("**/login")
    login_form = page.locator("form")
    field(login_form, "学号 / 邮箱").fill(STUDENT_ID)
    field(login_form, "密码").fill("Campus2026")
    page.get_by_role("checkbox").check()
    page.get_by_role("button", name="登录", exact=True).click()
    page.wait_for_url("**/dashboard")
    page.get_by_role("heading", name="荣誉数据概况").wait_for()

    page.get_by_role("button", name="荣誉证书管理", exact=True).click()
    page.wait_for_url("**/certificates")
    page.get_by_role("button", name="新增荣誉证书").click()
    modal = page.locator(".modal")
    modal.wait_for()
    field(modal, "荣誉证书名称").fill("河南省大学生创新创业大赛一等奖")
    field(modal, "颁发机构").fill("河南省教育厅")
    field(modal, "荣誉类型").select_option("INNOVATION_ENTREPRENEURSHIP")
    field(modal, "荣誉级别").select_option("PROVINCIAL_MUNICIPAL")
    field(modal, "奖项分类").select_option("INDIVIDUAL")
    field(modal, "取得日期").fill("2026-06-20")
    field(modal, "荣誉证书编号").fill("HN-IE-2026-001")
    field(modal, "备注").fill("大学生创新创业项目荣誉。")
    modal.locator("input[type=file]").set_input_files({
        "name": "honor-certificate.pdf",
        "mimeType": "application/pdf",
        "buffer": b"%PDF-1.4\n% honor archive smoke test\n%%EOF\n",
    })
    modal.get_by_role("button", name="收入档案").click()
    modal.wait_for(state="hidden")
    page.get_by_text("河南省大学生创新创业大赛一等奖").wait_for()
    page.screenshot(path=ARTIFACTS / "03-honor-certificate-archive.png", full_page=True)

    page.locator(".certificate-card").first.get_by_title("编辑").click()
    modal.wait_for()
    field(modal, "备注").fill("大学生创新创业项目荣誉，资料已归档。")
    modal.get_by_role("button", name="保存修改").click()
    modal.wait_for(state="hidden")

    page.get_by_role("button", name="荣誉数据概况", exact=True).click()
    page.wait_for_url("**/dashboard")
    page.wait_for_load_state("networkidle")
    page.get_by_text("河南省大学生创新创业大赛一等奖").wait_for()
    page.screenshot(path=ARTIFACTS / "04-dashboard-populated.png", full_page=True)

    page.get_by_role("button", name="荣誉数据分析", exact=True).click()
    page.wait_for_url("**/analytics")
    page.get_by_role("heading", name="荣誉数据分析").wait_for()

    page.get_by_role("button", name="账户设置", exact=True).click()
    page.wait_for_url("**/profile")
    profile_form = page.locator("form")
    field(profile_form, "学校").fill("河南农业大学")
    field(profile_form, "专业").fill("计算机科学与技术")
    field(profile_form, "预计毕业年份").fill("2028")
    page.get_by_role("button", name="保存信息").click()
    page.get_by_text("个人信息已保存").wait_for()

    page.set_viewport_size({"width": 390, "height": 844})
    page.reload()
    page.wait_for_load_state("networkidle")
    page.get_by_role("heading", name="个人信息").wait_for()
    page.screenshot(path=ARTIFACTS / "05-mobile-profile.png", full_page=True)

    page.set_viewport_size({"width": 1440, "height": 1000})
    page.reload()
    page.get_by_role("button", name="荣誉证书管理", exact=True).click()
    page.wait_for_url("**/certificates")
    page.locator(".certificate-card").first.get_by_title("删除").click()
    page.get_by_role("button", name="确认删除").click()
    page.get_by_text("荣誉证书档案已删除").wait_for()
    page.get_by_text("暂无荣誉证书记录").wait_for()

    if console_errors:
        raise AssertionError("Browser console errors: " + " | ".join(console_errors))

    print(f"E2E_SMOKE_OK register login honor archive upload edit delete profile mobile test_email={EMAIL}")
    browser.close()
