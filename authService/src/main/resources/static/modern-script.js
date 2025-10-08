// Check URL parameters on page load
window.addEventListener('DOMContentLoaded', () => {
  // Clear any existing result messages
  document.getElementById('result').textContent = '';
  document.getElementById('register-result').textContent = '';
  
  const urlParams = new URLSearchParams(window.location.search);
  const mode = urlParams.get('mode');
  
  if (mode === 'register') {
    showRegisterForm();
  } else {
    showLoginForm();
  }
});

function showLoginForm() {
  document.querySelector('.main-container').style.display = 'flex';
  document.getElementById('register-container').style.display = 'none';
  document.getElementById('result').textContent = '';
  document.getElementById('register-result').textContent = '';
  window.history.replaceState({}, '', '/');
}

function showRegisterForm() {
  document.querySelector('.main-container').style.display = 'none';
  document.getElementById('register-container').style.display = 'flex';
  document.getElementById('result').textContent = '';
  document.getElementById('register-result').textContent = '';
  window.history.replaceState({}, '', '/?mode=register');
}

// Toggle between login and register forms
document.getElementById('signup-link').addEventListener('click', (e) => {
  e.preventDefault();
  showRegisterForm();
});

document.getElementById('login-link').addEventListener('click', (e) => {
  e.preventDefault();
  showLoginForm();
});

// Login functionality
document.getElementById('btn-login').addEventListener('click', async () => {
  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value.trim();
  const out = document.getElementById('result');
  
  out.className = 'result';
  out.textContent = '';
  
  if (!username || !password) {
    out.textContent = 'Username and password required';
    return;
  }

  try {
    const res = await fetch('/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    if (!res.ok) {
      const text = await res.text();
      out.textContent = text || 'Login failed';
      return;
    }

    const json = await res.json();
    const token = json && json.token;
    
    if (token) {
      localStorage.setItem('sf_token', token);
      out.classList.add('success');
      out.textContent = 'Login successful! Redirecting...';
      
      setTimeout(() => {
        window.location.href = 'http://localhost:8080/';
      }, 1500);
    } else {
      out.textContent = 'No token returned';
    }
  } catch (err) {
    out.textContent = err.message || 'Error occurred';
  }
});

// Register functionality  
document.getElementById('btn-register').addEventListener('click', async () => {
  const username = document.getElementById('reg-username').value.trim();
  const email = document.getElementById('reg-email').value.trim();
  const password = document.getElementById('reg-password').value.trim();
  const out = document.getElementById('register-result');
  
  out.className = 'result';
  out.textContent = '';
  
  if (!username || !email || !password) {
    out.textContent = 'All fields are required';
    return;
  }

  // Basic email validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    out.textContent = 'Please enter a valid email address';
    return;
  }

  try {
    const res = await fetch('/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password })
    });

    if (res.ok) {
      const data = await res.json();
      out.classList.add('success');
      out.textContent = `Account created successfully for ${data.username}!`;
      
      // Clear form
      document.getElementById('reg-username').value = '';
      document.getElementById('reg-email').value = '';
      document.getElementById('reg-password').value = '';
      
      // Auto switch to login after 2 seconds
      setTimeout(() => {
        document.getElementById('register-container').style.display = 'none';
        document.querySelector('.main-container').style.display = 'flex';
        document.getElementById('username').value = username; // Pre-fill username
        out.textContent = '';
      }, 2000);
    } else {
      const text = await res.text();
      out.textContent = text || 'Registration failed';
    }
  } catch (err) {
    out.textContent = err.message || 'Error occurred';
  }
});

// Enter key support
document.addEventListener('keypress', (e) => {
  if (e.key === 'Enter') {
    const activeContainer = document.querySelector('.main-container:not([style*="display: none"])');
    
    if (activeContainer === document.querySelector('.main-container')) {
      // Login form is active
      document.getElementById('btn-login').click();
    } else {
      // Register form is active
      document.getElementById('btn-register').click();
    }
  }
});