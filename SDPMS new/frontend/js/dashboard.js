const user = getStoredUser();
if (!user || !getToken()) {
  window.location.href = 'index.html';
}

document.getElementById('userName').textContent = user.fullName || user.username;
document.getElementById('userRole').textContent = `(${user.role})`;
document.getElementById('logoutBtn').onclick = () => {
  clearAuth();
  window.location.href = 'index.html';
};

const role = user.role;
if (role === 'ADMIN') document.getElementById('navAdmin').classList.remove('hidden');
else if (role === 'FACULTY') document.getElementById('navFaculty').classList.remove('hidden');
else if (role === 'STUDENT') document.getElementById('navStudent').classList.remove('hidden');

const content = document.getElementById('content');
const pageTitle = document.getElementById('pageTitle');

function showPage(name) {
  document.querySelectorAll('.sidebar nav a').forEach(a => {
    a.classList.toggle('active', a.dataset.page === name);
  });
  pageTitle.textContent = titles[name] || name;
  if (pages[name]) pages[name]();
}

const titles = {
  home: 'Home',
  departments: 'Departments',
  students: 'Students',
  faculty: 'Faculty',
  subjects: 'Subjects',
  attendance: 'Mark Attendance',
  marks: 'Enter Marks',
  'class-report': 'Class Report',
  progress: 'My Progress',
  alerts: 'Notifications',
};

async function homePage() {
  content.innerHTML = `
    <div class="card">
      <h3>Welcome</h3>
      <p>You are logged in as <strong>${user.fullName || user.username}</strong> (${user.role}).</p>
      ${role === 'STUDENT' ? '<p>Use <strong>My Progress</strong> to view your attendance, marks, and grades.</p>' : ''}
      ${role === 'FACULTY' ? '<p>Use <strong>Attendance</strong> and <strong>Marks</strong> to manage class records.</p>' : ''}
      ${role === 'ADMIN' ? '<p>Manage departments, students, faculty, and subjects from the sidebar.</p>' : ''}
    </div>`;
}

async function studentProgressPage() {
  content.innerHTML = '<p>Loading your progress…</p>';
  try {
    const progress = await api('/student/progress');
    const attClass = (progress.attendancePercentage || 0) < 75 ? 'low' : '';
    content.innerHTML = `
      <div class="progress-grid">
        <div class="progress-card attendance ${attClass}">
          <div class="label">Attendance %</div>
          <div class="value">${(progress.attendancePercentage ?? 0).toFixed(1)}%</div>
        </div>
        <div class="progress-card sgpa">
          <div class="label">SGPA (Semester ${progress.currentSemester || '-'})</div>
          <div class="value">${progress.sgpa != null ? progress.sgpa : '-'}</div>
        </div>
        <div class="progress-card">
          <div class="label">Department</div>
          <div class="value" style="font-size:1rem">${progress.departmentName || '-'}</div>
        </div>
        <div class="progress-card">
          <div class="label">Roll No.</div>
          <div class="value" style="font-size:1rem">${progress.rollNumber || '-'}</div>
        </div>
      </div>
      <div class="card">
        <h3>Marks &amp; Grades (Semester ${progress.currentSemester || '-'})</h3>
        <div class="table-wrap">
          <table>
            <thead>
              <tr><th>Subject</th><th>Code</th><th>Internal</th><th>Semester</th><th>Total</th><th>Grade</th><th>GP</th></tr>
            </thead>
            <tbody>
              ${(progress.marksBySubject || []).map(m => `
                <tr>
                  <td>${m.subjectName || '-'}</td>
                  <td>${m.subjectCode || '-'}</td>
                  <td>${m.internalMarks ?? '-'}</td>
                  <td>${m.semesterMarks ?? '-'}</td>
                  <td>${m.totalMarks ?? '-'}</td>
                  <td>${m.grade || '-'}</td>
                  <td>${m.gradePoint ?? '-'}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
        ${!(progress.marksBySubject && progress.marksBySubject.length) ? '<p class="text-muted" style="margin-top:0.5rem">No marks recorded yet.</p>' : ''}
      </div>
      <div class="card">
        <h3>Recent Alerts</h3>
        <ul class="alert-list">
          ${(progress.alerts || []).slice(0, 5).map(a => `
            <li class="${a.readStatus ? 'read' : ''}">
              <div class="title">${a.title || a.type}</div>
              <div class="msg">${a.message || ''}</div>
            </li>
          `).join('')}
        </ul>
        ${!(progress.alerts && progress.alerts.length) ? '<p class="text-muted">No alerts.</p>' : ''}
      </div>`;
  } catch (e) {
    content.innerHTML = `<p class="error-msg">Failed to load progress: ${e.message}</p>`;
  }
}

async function alertsPage() {
  content.innerHTML = '<p>Loading notifications…</p>';
  try {
    const alerts = await api('/student/alerts');
    content.innerHTML = `
      <div class="card">
        <h3>Notifications</h3>
        <ul class="alert-list">
          ${(alerts || []).map(a => `
            <li class="${a.readStatus ? 'read' : ''}" data-id="${a.id}">
              <div class="title">${a.title || a.type}</div>
              <div class="msg">${a.message || ''}</div>
            </li>
          `).join('')}
        </ul>
        ${!(alerts && alerts.length) ? '<p class="text-muted">No notifications.</p>' : ''}
      </div>`;
  } catch (e) {
    content.innerHTML = `<p class="error-msg">Failed to load: ${e.message}</p>`;
  }
}

async function departmentsPage() {
  content.innerHTML = '<p>Loading…</p>';
  try {
    const list = await api('/admin/departments');
    content.innerHTML = `
      <div class="card">
        <h3>Departments</h3>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Name</th><th>Code</th></tr></thead>
            <tbody>
              ${(list || []).map(d => `<tr><td>${d.name}</td><td>${d.code || '-'}</td></tr>`).join('')}
            </tbody>
          </table>
        </div>
      </div>`;
  } catch (e) {
    content.innerHTML = `<p class="error-msg">${e.message}</p>`;
  }
}

async function studentsPage() {
  content.innerHTML = '<p>Loading…</p>';
  try {
    const list = await api('/admin/students');
    content.innerHTML = `
      <div class="card">
        <h3>Students</h3>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Roll No.</th><th>Name</th><th>Department</th><th>Sem</th><th>Email</th></tr></thead>
            <tbody>
              ${(list || []).map(s => `<tr><td>${s.rollNumber || '-'}</td><td>${s.fullName || s.username}</td><td>${s.departmentName || '-'}</td><td>${s.currentSemester || '-'}</td><td>${s.email || '-'}</td></tr>`).join('')}
            </tbody>
          </table>
        </div>
      </div>`;
  } catch (e) {
    content.innerHTML = `<p class="error-msg">${e.message}</p>`;
  }
}

async function facultyPage() {
  content.innerHTML = '<p>Loading…</p>';
  try {
    const list = await api('/admin/faculty');
    content.innerHTML = `
      <div class="card">
        <h3>Faculty</h3>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Employee ID</th><th>Name</th><th>Department</th><th>Email</th></tr></thead>
            <tbody>
              ${(list || []).map(f => `<tr><td>${f.employeeId || '-'}</td><td>${f.fullName || f.username}</td><td>${f.departmentName || '-'}</td><td>${f.email || '-'}</td></tr>`).join('')}
            </tbody>
          </table>
        </div>
      </div>`;
  } catch (e) {
    content.innerHTML = `<p class="error-msg">${e.message}</p>`;
  }
}

async function subjectsPage() {
  content.innerHTML = '<p>Loading…</p>';
  try {
    const depts = await api('/admin/departments');
    const firstDept = (depts && depts[0]) ? depts[0].id : null;
    const list = firstDept ? await api(`/admin/subjects?departmentId=${firstDept}`) : [];
    content.innerHTML = `
      <div class="card">
        <h3>Subjects</h3>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Name</th><th>Code</th><th>Semester</th><th>Credits</th></tr></thead>
            <tbody>
              ${(list || []).map(s => `<tr><td>${s.name}</td><td>${s.code || '-'}</td><td>${s.semester || '-'}</td><td>${s.credits || '-'}</td></tr>`).join('')}
            </tbody>
          </table>
        </div>
      </div>`;
  } catch (e) {
    content.innerHTML = `<p class="error-msg">${e.message}</p>`;
  }
}

async function attendancePage() {
  if (role !== 'FACULTY' && role !== 'ADMIN') return;
  content.innerHTML = `
    <div class="card">
      <h3>Mark Attendance</h3>
      <p>Select department and semester to load students, then subject and date to mark attendance.</p>
      <p class="text-muted" style="margin-top:0.5rem">Use Faculty/Admin APIs for bulk attendance (e.g. from Postman or a future form).</p>
    </div>`;
}

async function marksPage() {
  content.innerHTML = `
    <div class="card">
      <h3>Enter Marks</h3>
      <p>Marks entry is available via API. Use POST /api/faculty/marks with studentId, subjectId, semester, internalMarks, semesterMarks.</p>
    </div>`;
}

async function classReportPage() {
  content.innerHTML = `
    <div class="card">
      <h3>Class Report</h3>
      <p>Select department and semester to view class-wise performance (API: /api/faculty/students, /api/faculty/marks).</p>
    </div>`;
}

const pages = {
  home: homePage,
  departments: departmentsPage,
  students: studentsPage,
  faculty: facultyPage,
  subjects: subjectsPage,
  attendance: attendancePage,
  marks: marksPage,
  'class-report': classReportPage,
  progress: studentProgressPage,
  alerts: alertsPage,
};

document.querySelectorAll('.sidebar nav a[data-page]').forEach(a => {
  a.addEventListener('click', (e) => {
    e.preventDefault();
    showPage(a.dataset.page);
  });
});

if (role === 'STUDENT') {
  showPage('progress');
} else {
  showPage('home');
}
