document.getElementById('loginForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value;
  const errorEl = document.getElementById('errorMsg');
  const btn = document.getElementById('submitBtn');

  errorEl.classList.add('hidden');
  errorEl.textContent = '';
  btn.disabled = true;

  try {
    const res = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
      throw new Error(data.error || 'Login failed');
    }
    storeAuth(data);
    window.location.href = 'dashboard.html';
  } catch (err) {
    errorEl.textContent = err.message || 'Invalid username or password';
    errorEl.classList.remove('hidden');
  } finally {
    btn.disabled = false;
  }
});
