const API_BASE = 'http://localhost:8080/api';

function getToken() {
  return localStorage.getItem('sdpms_token');
}

function getAuthHeaders() {
  const token = getToken();
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

async function api(path, options = {}) {
  const url = `${API_BASE}${path}`;
  const res = await fetch(url, {
    ...options,
    headers: { ...getAuthHeaders(), ...options.headers },
  });
  if (res.status === 401) {
    localStorage.removeItem('sdpms_token');
    localStorage.removeItem('sdpms_user');
    window.location.href = 'index.html';
    throw new Error('Unauthorized');
  }
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }));
    throw new Error(err.error || res.statusText);
  }
  if (res.status === 204) return null;
  return res.json();
}

function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem('sdpms_user') || 'null');
  } catch {
    return null;
  }
}

function storeAuth(data) {
  localStorage.setItem('sdpms_token', data.token);
  localStorage.setItem('sdpms_user', JSON.stringify({
    username: data.username,
    role: data.role,
    userId: data.userId,
    fullName: data.fullName,
    departmentId: data.departmentId,
    departmentName: data.departmentName,
  }));
}

function clearAuth() {
  localStorage.removeItem('sdpms_token');
  localStorage.removeItem('sdpms_user');
}
