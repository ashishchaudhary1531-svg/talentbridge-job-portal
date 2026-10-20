import fs from "node:fs/promises";
import path from "node:path";
import { pathToFileURL } from "node:url";

const ROOT = "D:/Java Project";
const BUILD = path.join(ROOT, ".build", "presentation");
const OUTPUT = path.join(ROOT, "output");
const PRESENTATION_WORKSPACE = "C:/Users/ashis/.codex/visualizations/2026/10/08/01a11ced-4f00-7933-9a94-dd326f2b0a56";
const VALIDATED_OUTPUT = path.join(PRESENTATION_WORKSPACE, "output");
const VALIDATED_PPTX = path.join(VALIDATED_OUTPUT, "TalentBridge-Project-Presentation-v2.pptx");
const FINAL_PPTX = path.join(OUTPUT, "TalentBridge-Project-Presentation.pptx");
const PYTHON = "C:/Users/ashis/.cache/codex-runtimes/codex-primary-runtime/dependencies/python/python.exe";
const RUNTIME_NODE_MODULES = "C:/Users/ashis/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules";
const SKILL_DIR = "C:/Users/ashis/.codex/plugins/cache/openai-primary-runtime/presentations/26.1007.11041/skills/presentations";
const { Presentation, PresentationFile } = await import(pathToFileURL(path.join(RUNTIME_NODE_MODULES, "@oai/artifact-tool/dist/artifact_tool.mjs")).href);
const { resolvePresentationFont, finalizePresentation } = await import(pathToFileURL(path.join(SKILL_DIR, "container_tools/artifact_tool_utils.mjs")).href);
process.env.RUNTIME_NODE_MODULES = RUNTIME_NODE_MODULES;
const FONT = resolvePresentationFont({ fontFamily: "Arial" });
const W = 1280, H = 720;
const C = {
  navy: "#0B1728", navy2: "#142844", blue: "#2F6FE4", blue2: "#5A94F2", cyan: "#6DE0CF",
  ink: "#18253A", muted: "#64748B", soft: "#8B98AA", paper: "#F6F8FC",
  line: "#D9E1EC", white: "#FFFFFF", teal: "#168B8D", tealSoft: "#E6F5F2",
  violet: "#725BC7", violetSoft: "#F0EDFA", green: "#188560", greenSoft: "#E6F5EE",
  amber: "#A66F18", amberSoft: "#FFF4DF", red: "#B54D50", redSoft: "#FFF0EF",
};

function rect(slide, name, x, y, w, h, fill, lineFill = "none", lineWidth = 0, radius = 0) {
  return slide.shapes.add({
    geometry: radius ? "roundRect" : "rect", name,
    position: { left: x, top: y, width: w, height: h }, fill,
    line: { style: "solid", fill: lineFill, width: lineWidth },
    ...(radius ? { borderRadius: radius } : {}),
  });
}

function text(slide, name, value, x, y, w, h, options = {}) {
  const box = slide.shapes.add({
    geometry: "textbox", name,
    position: { left: x, top: y, width: w, height: h },
    fill: "none", line: { style: "solid", fill: "none", width: 0 },
  });
  box.text = value;
  box.text.style = {
    typeface: FONT, fontSize: options.size ?? 20, color: options.color ?? C.ink,
    bold: options.bold ?? false, alignment: options.align ?? "left",
    verticalAlignment: options.valign ?? "middle", autoFit: "shrinkText",
    wrap: "square", insets: 0, lineSpacing: options.lineSpacing ?? 1.0,
  };
  return box;
}

function line(slide, name, x1, y1, x2, y2, color = C.line, width = 1.5) {
  const dx = x2 - x1, dy = y2 - y1;
  return slide.shapes.add({ geometry: "line", name,
    position: { left: Math.min(x1, x2), top: Math.min(y1, y2), width: Math.abs(dx), height: Math.abs(dy), verticalFlip: dx * dy < 0 },
    fill: "none", line: { style: "solid", fill: color, width } });
}

function header(slide, title, kicker, page) {
  text(slide, `kicker-${page}`, kicker.toUpperCase(), 68, 38, 700, 22, { size: 14, color: C.blue, bold: true });
  text(slide, `title-${page}`, title, 68, 67, 1110, 62, { size: 43, color: C.ink, bold: true });
  line(slide, `header-rule-${page}`, 68, 145, 1212, 145, C.line, 1);
}

function footer(slide, page) {
  line(slide, `footer-rule-${page}`, 68, 676, 1212, 676, C.line, 1);
  text(slide, `footer-left-${page}`, "TALENTBRIDGE  /  JAVA WEB PROJECT", 68, 684, 580, 20, { size: 12, color: C.soft, bold: true });
  text(slide, `footer-right-${page}`, `${String(page).padStart(2, "0")}  /  07`, 1100, 684, 112, 20, { size: 12, color: C.soft, align: "right" });
}

function stepNode(slide, idx, x, titleValue, copy, color) {
  const circle = slide.shapes.add({ geometry: "ellipse", name: `step-${idx}-dot`, position: { left: x, top: 232, width: 48, height: 48 }, fill: color, line: { style: "solid", fill: color, width: 1 } });
  circle.text = String(idx).padStart(2, "0");
  circle.text.style = { typeface: FONT, fontSize: 17, color: C.white, bold: true, alignment: "center", verticalAlignment: "middle", insets: 0 };
  text(slide, `step-${idx}-title`, titleValue, x - 12, 298, 185, 48, { size: 22, bold: true });
  text(slide, `step-${idx}-copy`, copy, x - 12, 354, 190, 100, { size: 17, color: C.muted, valign: "top", lineSpacing: 1.1 });
  return circle;
}

function entity(slide, id, x, y, w, titleValue, fields, tint) {
  const height = 45 + fields.length * 30;
  rect(slide, `${id}-body`, x, y, w, height, C.white, C.line, 1, 7);
  rect(slide, `${id}-head`, x, y, w, 40, tint, tint, 0, 7);
  text(slide, `${id}-title`, titleValue, x + 14, y + 3, w - 28, 31, { size: 18, bold: true, color: C.navy });
  fields.forEach((field, i) => text(slide, `${id}-field-${i}`, field, x + 14, y + 46 + i * 28, w - 26, 24, { size: 15, color: field.includes("PK") ? C.blue : C.muted, bold: field.includes("PK") }));
  return { shape: slide.shapes.items[slide.shapes.items.length - fields.length - 2], x, y, w, h: height };
}

function addEntity(slide, id, x, y, w, titleValue, fields, tint) {
  const height = 45 + fields.length * 30;
  const body = rect(slide, `${id}-body`, x, y, w, height, C.white, C.line, 1, 7);
  rect(slide, `${id}-head`, x, y, w, 40, tint, tint, 0, 7);
  text(slide, `${id}-title`, titleValue, x + 14, y + 3, w - 28, 31, { size: 18, bold: true, color: C.navy });
  fields.forEach((field, i) => text(slide, `${id}-field-${i}`, field, x + 14, y + 46 + i * 28, w - 26, 24, { size: 15, color: field.includes("PK") ? C.blue : C.muted, bold: field.includes("PK") }));
  return { shape: body, x, y, w, h: height };
}

const deck = Presentation.create({ slideSize: { width: W, height: H } });

// 1 — A restrained cover with a three-role relationship graphic.
{
  const s = deck.slides.add(); s.background.fill = C.navy;
  rect(s, "cover-accent", 68, 101, 7, 500, C.blue);
  text(s, "cover-kicker", "JAVA WEB PROJECT  ·  PROJECT REVIEW", 100, 112, 620, 26, { size: 15, color: "#A9C8FA", bold: true });
  text(s, "cover-title", "TalentBridge\nJob Portal", 100, 174, 690, 170, { size: 62, color: C.white, bold: true, lineSpacing: 0.96 });
  text(s, "cover-subtitle", "A role-based marketplace for hiring and job search", 104, 371, 630, 54, { size: 24, color: "#D3DEEE" });
  text(s, "cover-stack", "SERVLETS  /  JSP  /  JDBC  /  SQLITE", 104, 445, 610, 28, { size: 16, color: "#8FA9CC", bold: true });
  line(s, "cover-connector-1", 850, 287, 1025, 198, "#5A94F2", 2);
  line(s, "cover-connector-2", 850, 287, 1025, 390, "#5A94F2", 2);
  line(s, "cover-connector-3", 868, 288, 1116, 288, "#5A94F2", 1.5);
  const nodes = [
    { x: 798, y: 253, label: "PORTAL", color: C.blue },
    { x: 1034, y: 150, label: "EMPLOYER", color: C.teal },
    { x: 1034, y: 354, label: "JOB SEEKER", color: C.violet },
    { x: 1116, y: 253, label: "ADMIN", color: "#D09235" },
  ];
  for (const [i, n] of nodes.entries()) {
    const c = s.shapes.add({ geometry: "ellipse", name: `cover-node-${i}`, position: { left: n.x, top: n.y, width: 70, height: 70 }, fill: n.color, line: { style: "solid", fill: n.color, width: 1 } });
    c.text = i === 0 ? "TB" : String(i).padStart(2, "0");
    c.text.style = { typeface: FONT, fontSize: 19, color: C.white, bold: true, alignment: "center", verticalAlignment: "middle", insets: 0 };
    text(s, `cover-node-label-${i}`, n.label, n.x - 26, n.y + 78, 122, 23, { size: 12, color: "#B9C9DF", bold: true, align: "center" });
  }
  line(s, "cover-bottom-rule", 100, 630, 1180, 630, "#273950", 1);
  text(s, "cover-lower", "PLANNING  ·  DATA MODEL  ·  RESPONSIVE INTERFACE", 100, 643, 800, 25, { size: 13, color: "#8498B4", bold: true });
}

// 2 — Scope and the three account types.
{
  const s = deck.slides.add(); s.background.fill = C.paper; header(s, "Product scope", "Problem and users", 2);
  text(s, "scope-intro", "One portal coordinates job discovery, hiring decisions, and candidate communication.", 68, 168, 1100, 50, { size: 23, color: C.muted });
  const columns = [
    { x: 72, color: C.blue, num: "01", title: "Job seeker", lead: "Find a role and follow through", copy: "Search approved listings by keyword, location, and category. Apply with a note and resume, track status, and message the employer." },
    { x: 475, color: C.teal, num: "02", title: "Employer", lead: "Hire with a clear process", copy: "Create and edit openings, review applicants, update application stages, and keep candidate conversations together." },
    { x: 878, color: C.violet, num: "03", title: "Administrator", lead: "Keep the marketplace reliable", copy: "Approve postings, manage accounts and roles, update portal settings, and review activity and status summaries." },
  ];
  for (const [i, col] of columns.entries()) {
    if (i > 0) line(s, `scope-divider-${i}`, col.x - 26, 259, col.x - 26, 574, C.line, 1);
    text(s, `scope-num-${i}`, col.num, col.x, 248, 80, 32, { size: 15, color: col.color, bold: true });
    text(s, `scope-title-${i}`, col.title, col.x, 290, 330, 48, { size: 30, bold: true });
    rect(s, `scope-mark-${i}`, col.x, 347, 44, 4, col.color);
    text(s, `scope-lead-${i}`, col.lead, col.x, 374, 330, 52, { size: 19, bold: true });
    text(s, `scope-copy-${i}`, col.copy, col.x, 432, 335, 132, { size: 18, color: C.muted, valign: "top", lineSpacing: 1.12 });
  }
  text(s, "scope-added", "ADDED FOR THE REVIEW  ·  First-run demo data  ·  Role-based access  ·  Resume handling  ·  Responsive layout", 72, 604, 1130, 34, { size: 15, color: C.blue, bold: true });
  footer(s, 2);
}

// 3 — Editable architecture diagram.
{
  const s = deck.slides.add(); s.background.fill = C.paper; header(s, "Application architecture", "Project structure", 3);
  text(s, "arch-subtitle", "A servlet MVC flow keeps presentation, workflow rules, and persistence in separate layers.", 68, 162, 1120, 38, { size: 20, color: C.muted });
  const layers = [
    { x: 74, fill: "#EAF1FE", stroke: "#BED1F7", title: "Browser", detail: "Responsive JSP views\nHTML forms · CSS" },
    { x: 365, fill: C.tealSoft, stroke: "#B6E0D8", title: "Web layer", detail: "Servlet controllers\nSession · CSRF · role checks" },
    { x: 656, fill: C.violetSoft, stroke: "#D4CDED", title: "Data access", detail: "JDBC DAO classes\nPrepared statements" },
    { x: 947, fill: "#EEF2F6", stroke: "#D7DFE8", title: "SQLite", detail: "Relational schema\nForeign keys · indexes" },
  ];
  const boxes = [];
  for (const [i, layer] of layers.entries()) {
    const b = rect(s, `arch-layer-${i}`, layer.x, 270, 220, 138, layer.fill, layer.stroke, 1.4, 10);
    text(s, `arch-title-${i}`, layer.title, layer.x + 16, 285, 188, 34, { size: 23, bold: true });
    text(s, `arch-detail-${i}`, layer.detail, layer.x + 16, 329, 188, 62, { size: 16, color: C.muted, valign: "top", lineSpacing: 1.12 });
    boxes.push(b);
  }
  for (let i = 0; i < boxes.length - 1; i++) s.shapes.connect(boxes[i], boxes[i + 1], { kind: "straight", fromSide: "right", toSide: "left", line: { style: "solid", fill: C.blue, width: 2 }, tail: { type: "triangle", width: "sm", length: "sm" } });
  text(s, "arch-flow-label-1", "request", 290, 249, 80, 22, { size: 13, color: C.blue, bold: true, align: "center" });
  text(s, "arch-flow-label-2", "validated action", 566, 249, 105, 22, { size: 13, color: C.blue, bold: true, align: "center" });
  text(s, "arch-flow-label-3", "SQL", 861, 249, 58, 22, { size: 13, color: C.blue, bold: true, align: "center" });
  const worker = rect(s, "arch-worker", 655, 465, 220, 85, "#FFF4DF", "#E8D3A9", 1.2, 9);
  text(s, "arch-worker-title", "Managed workers", 670, 475, 190, 27, { size: 19, bold: true });
  text(s, "arch-worker-detail", "Activity writes · expired-job cleanup", 670, 507, 190, 34, { size: 14, color: C.muted });
  s.shapes.connect(boxes[2], worker, { kind: "straight", fromSide: "bottom", toSide: "top", line: { style: "dashed", fill: C.amber, width: 1.8 }, tail: { type: "triangle", width: "sm", length: "sm" } });
  const controls = [
    { x: 112, label: "PBKDF2 password hashes", color: C.blue },
    { x: 474, label: "Private resume files", color: C.teal },
    { x: 818, label: "Polymorphic user roles", color: C.violet },
  ];
  for (const [i, item] of controls.entries()) {
    rect(s, `arch-control-line-${i}`, item.x, 592, 4, 29, item.color);
    text(s, `arch-control-${i}`, item.label, item.x + 15, 590, 300, 32, { size: 16, color: C.muted, bold: true });
  }
  footer(s, 3);
}

// 4 — Editable relational data model.
{
  const s = deck.slides.add(); s.background.fill = C.paper; header(s, "Database design", "Relational model · SQLite", 4);
  text(s, "db-caption", "Foreign keys connect the hiring workflow; unique constraints prevent duplicate applications.", 68, 162, 1120, 38, { size: 19, color: C.muted });
  const U = addEntity(s, "users", 70, 232, 225, "users", ["PK  id", "email · role · active", "name · title · company"], "#EAF1FE");
  const J = addEntity(s, "jobs", 365, 205, 230, "jobs", ["PK  id", "FK  employer_id", "title · status · expires_at"], C.tealSoft);
  const A = addEntity(s, "applications", 670, 232, 245, "applications", ["PK  id", "FK  job_id · seeker_id", "status · resume_path"], C.violetSoft);
  const M = addEntity(s, "messages", 985, 205, 220, "messages", ["PK  id", "FK  application_id", "sender_id · body"], C.greenSoft);
  const L = addEntity(s, "activity_log", 365, 466, 230, "activity_log", ["PK  id", "FK  actor_id", "description · created_at"], C.amberSoft);
  const S = addEntity(s, "settings", 670, 466, 245, "system_settings", ["PK  setting_key", "setting_value"], "#EEF2F6");
  s.shapes.connect(U.shape, J.shape, { kind: "straight", fromSide: "right", toSide: "left", line: { style: "solid", fill: C.blue, width: 1.8 }, tail: { type: "triangle", width: "sm", length: "sm" } });
  s.shapes.connect(J.shape, A.shape, { kind: "straight", fromSide: "right", toSide: "left", line: { style: "solid", fill: C.blue, width: 1.8 }, tail: { type: "triangle", width: "sm", length: "sm" } });
  s.shapes.connect(A.shape, M.shape, { kind: "straight", fromSide: "right", toSide: "left", line: { style: "solid", fill: C.blue, width: 1.8 }, tail: { type: "triangle", width: "sm", length: "sm" } });
  s.shapes.connect(U.shape, A.shape, { kind: "elbow", fromSide: "bottom", toSide: "top", line: { style: "dashed", fill: C.violet, width: 1.6 }, tail: { type: "triangle", width: "sm", length: "sm" } });
  s.shapes.connect(U.shape, L.shape, { kind: "elbow", fromSide: "bottom", toSide: "left", line: { style: "dashed", fill: C.amber, width: 1.6 }, tail: { type: "triangle", width: "sm", length: "sm" } });
  text(s, "db-rel-1", "employer_id", 294, 215, 70, 20, { size: 12, color: C.blue, bold: true, align: "center" });
  text(s, "db-rel-2", "job_id", 600, 215, 65, 20, { size: 12, color: C.blue, bold: true, align: "center" });
  text(s, "db-rel-3", "application_id", 914, 215, 75, 20, { size: 12, color: C.blue, bold: true, align: "center" });
  text(s, "db-rel-4", "seeker_id", 568, 321, 78, 20, { size: 12, color: C.violet, bold: true, align: "center" });
  text(s, "db-rel-5", "actor_id", 298, 442, 62, 20, { size: 12, color: C.amber, bold: true, align: "center" });
  text(s, "db-key-note", "1 → many relationships shown by the FK arrows", 70, 610, 680, 27, { size: 14, color: C.muted });
  footer(s, 4);
}

// 5 — End-to-end job and application workflow.
{
  const s = deck.slides.add(); s.background.fill = C.paper; header(s, "Hiring workflow", "Role handoffs", 5);
  text(s, "flow-subtitle", "A posting must pass review before candidates can apply.", 68, 162, 1040, 38, { size: 21, color: C.muted });
  const centers = [105, 340, 575, 810, 1045];
  const steps = [
    ["Employer submits", "Adds role, location,\ntype, salary, skills", C.teal],
    ["Admin reviews", "Approves or rejects\nthe job listing", C.violet],
    ["Seeker applies", "Searches, adds a note,\nand uploads a resume", C.blue],
    ["Employer reviews", "Updates status from\nreview to offer or hire", C.teal],
    ["Both communicate", "Application messages\nkeep next steps together", C.green],
  ];
  const nodes = steps.map((step, i) => stepNode(s, i + 1, centers[i], step[0], step[1], step[2]));
  for (let i = 0; i < nodes.length - 1; i++) s.shapes.connect(nodes[i], nodes[i + 1], { kind: "straight", fromSide: "right", toSide: "left", line: { style: "solid", fill: "#9DABC0", width: 1.8 }, tail: { type: "triangle", width: "sm", length: "sm" } });
  rect(s, "flow-status-band", 72, 492, 1135, 117, C.white, C.line, 1, 8);
  text(s, "flow-status-label", "APPLICATION STATES", 94, 506, 220, 24, { size: 13, color: C.blue, bold: true });
  text(s, "flow-status-sequence", "In review   →   Shortlisted   →   Interview   →   Offer   →   Hired", 94, 541, 1030, 32, { size: 22, bold: true });
  text(s, "flow-status-note", "Rejected and withdrawn outcomes are also recorded.", 94, 576, 800, 20, { size: 14, color: C.muted });
  footer(s, 5);
}

// 6 — Exact Java Web rubric with evidence mapping.
{
  const s = deck.slides.add(); s.background.fill = C.paper; header(s, "Java Web rubric mapping", "Assessment evidence · 33 marks", 6);
  const x0 = 70, y0 = 178, widths = [54, 440, 115, 515], rowH = 80;
  const headers = ["#", "Review area", "Marks", "Project evidence"];
  let x = x0;
  headers.forEach((h, i) => { text(s, `rubric-h-${i}`, h.toUpperCase(), x + 8, y0, widths[i] - 16, 31, { size: 13, color: C.soft, bold: true }); x += widths[i]; });
  line(s, "rubric-head-rule", x0, y0 + 35, 1210, y0 + 35, C.ink, 1.6);
  const rows = [
    ["01", "Problem understanding & solution design", "8", "Three account roles, job approval, application stages, and schema planned as one workflow."],
    ["02", "Core Java concepts", "10", "Abstract User with role subclasses; generic Repository<T, ID>; enums, validation, and PBKDF2 hashing."],
    ["03", "Database integration (JDBC)", "8", "SQLite schema, connection setup, prepared statements, and focused DAO classes."],
    ["04", "Servlets & web integration", "7", "Jakarta Servlet 6 filters/controllers, JSP views, session security, and multipart resumes."],
  ];
  rows.forEach((row, r) => {
    const y = y0 + 48 + r * rowH;
    if (r % 2 === 0) rect(s, `rubric-row-bg-${r}`, x0, y, 1140, rowH - 4, C.white, "none", 0, 5);
    text(s, `rubric-num-${r}`, row[0], x0 + 8, y + 13, 38, 40, { size: 17, color: C.blue, bold: true });
    text(s, `rubric-area-${r}`, row[1], x0 + widths[0] + 8, y + 10, widths[1] - 18, 54, { size: 19, bold: true });
    text(s, `rubric-mark-${r}`, row[2], x0 + widths[0] + widths[1] + 14, y + 9, 80, 54, { size: 25, color: C.blue, bold: true });
    text(s, `rubric-proof-${r}`, row[3], x0 + widths[0] + widths[1] + widths[2] + 12, y + 9, widths[3] - 25, 58, { size: 16, color: C.muted });
  });
  line(s, "rubric-total-line", 70, 557, 1210, 557, C.line, 1);
  text(s, "rubric-total", "TOTAL", 70, 568, 470, 38, { size: 17, color: C.ink, bold: true });
  text(s, "rubric-total-number", "33 marks", 980, 568, 220, 38, { size: 20, color: C.blue, bold: true, align: "right" });
  text(s, "rubric-extension", "Additional Java topics: managed background threads · collections and generics · exception handling", 70, 621, 1130, 31, { size: 15, color: C.teal, bold: true });
  footer(s, 6);
}

// 7 — A reproducible local demo path.
{
  const s = deck.slides.add(); s.background.fill = C.navy; 
  text(s, "run-kicker", "LOCAL DEMO", 68, 44, 380, 22, { size: 14, color: "#A9C8FA", bold: true });
  text(s, "run-title", "Run and explore", 68, 76, 900, 58, { size: 46, color: C.white, bold: true });
  text(s, "run-subtitle", "First launch builds the WAR, creates the database, and seeds a working demo.", 68, 143, 1090, 34, { size: 20, color: "#C4D2E5" });
  const steps = [
    { n: "01", title: "Start the portal", body: "JDK 17+\nDouble-click run.bat\nFirst launch needs internet for Maven and dependencies." },
    { n: "02", title: "Open the app", body: "http://127.0.0.1:8080\nThe server runs on your computer.\nKeep the launch window open." },
  ];
  steps.forEach((it, i) => {
    const x = 68 + i * 365;
    text(s, `run-step-n-${i}`, it.n, x, 224, 50, 32, { size: 16, color: C.cyan, bold: true });
    text(s, `run-step-title-${i}`, it.title, x, 263, 305, 42, { size: 25, color: C.white, bold: true });
    text(s, `run-step-body-${i}`, it.body, x, 316, 310, 96, { size: 17, color: "#C3D0E0", valign: "top", lineSpacing: 1.15 });
  });
  line(s, "run-vertical-divider", 430, 224, 430, 420, "#344964", 1);
  rect(s, "run-demo-surface", 820, 218, 386, 345, C.navy2, "#324967", 1, 8);
  text(s, "run-demo-label", "DEMO ACCOUNTS", 846, 240, 330, 24, { size: 13, color: "#9AB8E7", bold: true });
  const accounts = [
    ["Administrator", "admin@talentbridge.demo"],
    ["Employer", "employer@talentbridge.demo"],
    ["Job seeker", "seeker@talentbridge.demo"],
  ];
  accounts.forEach((account, i) => {
    const y = 285 + i * 68;
    text(s, `run-role-${i}`, account[0], 846, y, 320, 22, { size: 15, color: "#C7D5E7", bold: true });
    text(s, `run-email-${i}`, account[1], 846, y + 24, 330, 24, { size: 16, color: C.white });
  });
  line(s, "run-demo-rule", 846, 492, 1178, 492, "#3A506C", 1);
  text(s, "run-password-label", "PASSWORD FOR EACH ACCOUNT", 846, 508, 330, 20, { size: 12, color: "#94A9C5", bold: true });
  text(s, "run-password", "demo1234", 846, 531, 330, 26, { size: 19, color: C.cyan, bold: true });
  line(s, "run-footer-rule", 68, 641, 1210, 641, "#2A3C54", 1);
  text(s, "run-footer-note", "Demo data is local. The database and uploaded resumes are kept under the project’s data/ folder.", 68, 654, 1100, 27, { size: 14, color: "#93A7C3" });
}

await fs.mkdir(BUILD, { recursive: true });
await fs.mkdir(OUTPUT, { recursive: true });
const previewDir = path.join(BUILD, "previews");
await fs.mkdir(previewDir, { recursive: true });

async function writeBlob(filePath, blob) {
  await fs.writeFile(filePath, new Uint8Array(await blob.arrayBuffer()));
}

for (const [index, slide] of deck.slides.items.entries()) {
  const png = await deck.export({ slide, format: "png", scale: 1 });
  await writeBlob(path.join(previewDir, `slide-${String(index + 1).padStart(2, "0")}.png`), png);
}

const stagingDir = path.join(BUILD, "finalizer");
await fs.mkdir(stagingDir, { recursive: true });
const validationBuildDir = path.join(PRESENTATION_WORKSPACE, "presentation-build");
await fs.mkdir(validationBuildDir, { recursive: true });
await fs.mkdir(VALIDATED_OUTPUT, { recursive: true });
const candidatePath = path.join(validationBuildDir, "candidate.pptx");
await (await PresentationFile.exportPptx(deck)).save(candidatePath);
const result = await finalizePresentation({
  explicitTotalSlideCount: 7,
  requiredNativeTableOwnerSlides: [],
  requiredNativeChartOwnerSlides: [],
  workspaceDir: PRESENTATION_WORKSPACE,
  candidatePath,
  finalPath: VALIDATED_PPTX,
  pythonExecutable: PYTHON,
  integrityValidatorPath: path.join(SKILL_DIR, "container_tools/inspect_presentation_package_integrity.py"),
  layoutValidatorPath: path.join(SKILL_DIR, "container_tools/inspect_presentation_layout_geometry.py"),
  layoutArgs: ["--expected-slide-size-emu", "12192000,6858000", "--validate-bullet-geometry", "--validate-heading-fit"],
  fontPolicy: { basis: "design", families: [FONT] },
  verifyArtifactToolImport: true,
  receiptPath: path.join(validationBuildDir, "TalentBridge-Project-Presentation-v2.validation.json"),
});
await fs.copyFile(VALIDATED_PPTX, FINAL_PPTX);
console.log(JSON.stringify({ finalPath: FINAL_PPTX, result }, null, 2));
