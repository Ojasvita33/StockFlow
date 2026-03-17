// ── UTILS ─────────────────────────────────────────────
function switchTo(view) {
  document.getElementById('login-view').style.display    = view === 'login'    ? 'block' : 'none';
  document.getElementById('register-view').style.display = view === 'register' ? 'block' : 'none';
  clearMsg();
  // Sync URL
  window.history.replaceState({}, '', view === 'register' ? '/?mode=register' : '/');
}

function clearMsg() {
  ['login-msg', 'register-msg'].forEach(id => {
    const el = document.getElementById(id);
    el.textContent = ''; el.className = 'msg';
  });
}

function setMsg(id, text, isError) {
  const el = document.getElementById(id);
  el.textContent = text;
  el.className = 'msg ' + (isError ? 'error' : 'success');
}

function togglePw(inputId, btn) {
  const input = document.getElementById(inputId);
  const isHidden = input.type === 'password';
  input.type = isHidden ? 'text' : 'password';
  btn.querySelector('i').className = isHidden ? 'fas fa-eye-slash' : 'fas fa-eye';
}

function setLoading(btnId, loading) {
  const btn = document.getElementById(btnId);
  btn.disabled = loading;
  btn.querySelector('span').textContent = loading ? 'Please wait...' : (btnId === 'btn-login' ? 'Sign In' : 'Create Account');
}

// ── PAGE INIT ─────────────────────────────────────────
window.addEventListener('DOMContentLoaded', () => {
  const mode = new URLSearchParams(window.location.search).get('mode');
  switchTo(mode === 'register' ? 'register' : 'login');

  // Enter key support
  document.addEventListener('keydown', e => {
    if (e.key !== 'Enter') return;
    const loginVisible = document.getElementById('login-view').style.display !== 'none';
    loginVisible ? doLogin() : doRegister();
  });
});

// ── LOGIN ─────────────────────────────────────────────
document.getElementById('btn-login').addEventListener('click', doLogin);

async function doLogin() {
  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value.trim();

  if (!username || !password) {
    setMsg('login-msg', 'Username and password are required.', true); return;
  }

  setLoading('btn-login', true);

  try {
    const res = await fetch('/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const text = await res.text();

    if (!res.ok) {
      setMsg('login-msg', text || 'Login failed. Check your credentials.', true);
      return;
    }

    const data = JSON.parse(text);
    const { token, role, companyId } = data;

    if (!token) { setMsg('login-msg', 'Login failed.', true); return; }

    setMsg('login-msg', 'Login successful! Redirecting...', false);

    // Pass token via URL params — localStorage is origin-scoped (different ports)
    setTimeout(() => {
      window.location.href =
        'http://localhost:8080/?token='     + encodeURIComponent(token) +
        '&role='      + encodeURIComponent(role) +
        '&username='  + encodeURIComponent(username) +
        '&companyId=' + encodeURIComponent(companyId || '');
    }, 800);

  } catch (err) {
    setMsg('login-msg', 'Could not connect to server.', true);
  } finally {
    setLoading('btn-login', false);
  }
}

// ── REGISTER ──────────────────────────────────────────
document.getElementById('btn-register').addEventListener('click', doRegister);

async function doRegister() {
  const companyName = document.getElementById('reg-company').value.trim();
  const username    = document.getElementById('reg-username').value.trim();
  const email       = document.getElementById('reg-email').value.trim();
  const password    = document.getElementById('reg-password').value.trim();

  if (!companyName || !username || !email || !password) {
    setMsg('register-msg', 'All fields are required.', true); return;
  }

  if (password.length < 6) {
    setMsg('register-msg', 'Password must be at least 6 characters.', true); return;
  }

  setLoading('btn-register', true);

  try {
    const res = await fetch('/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ companyName, username, email, password })
    });

    const text = await res.text();

    if (!res.ok) {
      setMsg('register-msg', text || 'Registration failed.', true); return;
    }

    const data = JSON.parse(text);
    setMsg('register-msg', 'Company "' + data.companyName + '" registered! Redirecting to login...', false);

    // Clear fields
    ['reg-company','reg-username','reg-email','reg-password'].forEach(id => {
      document.getElementById(id).value = '';
    });

    setTimeout(() => switchTo('login'), 1800);

  } catch (err) {
    setMsg('register-msg', 'Could not connect to server.', true);
  } finally {
    setLoading('btn-register', false);
  }
}
