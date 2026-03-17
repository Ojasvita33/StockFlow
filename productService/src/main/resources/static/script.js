// ── INIT ──────────────────────────────────────────────
const urlParams = new URLSearchParams(window.location.search);
if (urlParams.get('token')) {
  localStorage.setItem('sf_token',     urlParams.get('token'));
  localStorage.setItem('sf_role',      urlParams.get('role'));
  localStorage.setItem('sf_username',  urlParams.get('username'));
  localStorage.setItem('sf_companyId', urlParams.get('companyId'));
  window.history.replaceState({}, '', '/');
}

const token     = localStorage.getItem('sf_token');
const role      = localStorage.getItem('sf_role');
const username  = localStorage.getItem('sf_username');
const companyId = localStorage.getItem('sf_companyId');

if (!token) { window.location.href = 'http://localhost:8081/'; }

const AUTH_API    = 'http://localhost:8081';
const PRODUCT_API = 'http://localhost:8080/api/products';

const headers = {
  'Content-Type': 'application/json',
  'Authorization': 'Bearer ' + token
};

// ── ROLE SETUP ────────────────────────────────────────
const isAdmin = role === 'ROLE_ADMIN';
const isUser  = role === 'ROLE_USER';

function roleLabel(r) {
  if (r === 'ROLE_ADMIN')   return 'Admin';
  if (r === 'ROLE_COMPANY') return 'Company';
  if (r === 'ROLE_USER')    return 'User';
  return r;
}
function roleTagClass(r) {
  if (r === 'ROLE_ADMIN')   return 'tag role-admin';
  if (r === 'ROLE_COMPANY') return 'tag role-company';
  return 'tag role-user';
}

// ── SIDEBAR & TOPBAR SETUP ────────────────────────────
document.getElementById('topbar-username').textContent  = username;
document.getElementById('sidebar-username').textContent = username;
document.getElementById('sidebar-role').textContent     = roleLabel(role);
document.getElementById('user-avatar').textContent      = (username || '?')[0].toUpperCase();

if (isAdmin) {
  document.getElementById('nav-users').style.display    = 'flex';
  document.getElementById('add-product-card').style.display = 'block';
}

// Fetch and display company name
if (companyId && companyId !== 'null' && companyId !== '0') {
  fetch(AUTH_API + '/company/' + companyId + '/name')
    .then(r => r.ok ? r.json() : null)
    .then(data => {
      if (data && data.name) {
        document.getElementById('sidebar-company').textContent = data.name;
        document.getElementById('topbar-company').textContent  = data.name;
        document.title = 'StockFlow Inventory Management — ' + data.name;
      }
    })
    .catch(() => {});
}

// ── PAGE NAVIGATION ───────────────────────────────────
const pages = ['dashboard', 'products', 'lowstock', 'users'];

function showPage(name) {
  pages.forEach(p => {
    document.getElementById('page-' + p).classList.remove('active');
    const nav = document.getElementById('nav-' + p);
    if (nav) nav.classList.remove('active');
  });
  document.getElementById('page-' + name).classList.add('active');
  const nav = document.getElementById('nav-' + name);
  if (nav) nav.classList.add('active');

  const titles = { dashboard: 'Dashboard', products: 'Products', lowstock: 'Low Stock', users: 'Manage Users' };
  document.getElementById('page-title').textContent = titles[name] || name;

  if (name === 'dashboard') loadDashboard();
  if (name === 'products')  loadProducts();
  if (name === 'lowstock')  loadLowStock();
  if (name === 'users')     loadUsers();
}

// ── UTILS ─────────────────────────────────────────────
function rupee(n) {
  return '\u20B9' + Number(n).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function categoryTag(cat) {
  const map = { Electronics: 'tag-blue', Groceries: 'tag-green', Clothing: 'tag-purple', Stationery: 'tag-yellow', Furniture: 'tag-gray' };
  return '<span class="tag ' + (map[cat] || 'tag-gray') + '">' + cat + '</span>';
}

function setMsg(id, text, isError) {
  const el = document.getElementById(id);
  if (!el) return;
  el.textContent = text;
  el.className = 'form-msg ' + (isError ? 'error' : 'success');
  if (!isError) setTimeout(() => { el.textContent = ''; el.className = 'form-msg'; }, 3000);
}

async function apiFetch(url, opts) {
  const res = await fetch(url, { headers, ...opts });
  if (res.status === 401 || res.status === 403) {
    alert('Session expired. Please login again.');
    localStorage.clear();
    window.location.href = 'http://localhost:8081/';
  }
  return res;
}

// ── DASHBOARD ─────────────────────────────────────────
async function loadDashboard() {
  try {
    const [allRes, valueRes, catsRes] = await Promise.all([
      apiFetch(PRODUCT_API + '/page?page=0&size=1000'),
      apiFetch(PRODUCT_API + '/value/total'),
      apiFetch(PRODUCT_API + '/categories')
    ]);
    const allData   = await allRes.json();
    const valueData = await valueRes.json();
    const cats      = await catsRes.json();
    const products  = allData.content || [];
    const lowCount  = products.filter(p => p.quantity < 5).length;

    document.getElementById('stat-total').textContent      = allData.totalElements;
    document.getElementById('stat-value').textContent      = rupee(valueData.totalInventoryValue);
    document.getElementById('stat-lowstock').textContent   = lowCount;
    document.getElementById('stat-categories').textContent = cats.length;

    const badge = document.getElementById('lowstock-badge');
    badge.textContent = lowCount;
    badge.classList.toggle('hidden', lowCount === 0);

    const tbody  = document.getElementById('recent-tbody');
    const recent = products.slice(-5).reverse();
    if (!recent.length) {
      tbody.innerHTML = '<tr><td colspan="6"><div class="empty-state"><i class="fas fa-box-open"></i><p>No products yet</p></div></td></tr>';
    } else {
      tbody.innerHTML = recent.map((p, i) => `
        <tr>
          <td>${i + 1}</td>
          <td><strong>${p.name}</strong></td>
          <td>${categoryTag(p.category)}</td>
          <td class="${p.quantity < 5 ? 'qty-low' : 'qty-ok'}">${p.quantity}</td>
          <td>${rupee(p.pricePerUnit)}</td>
          <td>${rupee(p.quantity * p.pricePerUnit)}</td>
        </tr>`).join('');
    }
  } catch (e) { console.error(e); }
}

// ── PRODUCTS ──────────────────────────────────────────
let page = 0, totalPages = 1, allProducts = [];
const PAGE_SIZE = 10;

async function loadProducts(p) {
  if (p !== undefined) page = p;
  try {
    const res  = await apiFetch(`${PRODUCT_API}/page?page=${page}&size=${PAGE_SIZE}`);
    const data = await res.json();
    allProducts = data.content || [];
    page        = data.number;
    totalPages  = data.totalPages;
    renderProducts(allProducts);
    document.getElementById('page-info').textContent = `Page ${page + 1} of ${totalPages}`;
  } catch (e) { console.error(e); }
}

function actionButtons(p) {
  // Admin: Edit + Delete | User: Edit only
  const editBtn   = `<button class="btn btn-sm btn-edit"   onclick="openEditModal(${p.id},'${p.name}','${p.category}',${p.quantity},${p.pricePerUnit})"><i class="fas fa-pen"></i> Edit</button>`;
  const deleteBtn = `<button class="btn btn-sm btn-delete" onclick="openDeleteModal(${p.id})"><i class="fas fa-trash"></i></button>`;
  return isAdmin ? editBtn + ' ' + deleteBtn : (isUser ? editBtn : '—');
}

function renderProducts(products) {
  const tbody = document.getElementById('product-tbody');
  if (!products.length) {
    tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state"><i class="fas fa-box-open"></i><p>No products found</p></div></td></tr>';
    return;
  }
  tbody.innerHTML = products.map((p, i) => `
    <tr class="${p.quantity < 5 ? 'low-stock-row' : ''}">
      <td>${page * PAGE_SIZE + i + 1}</td>
      <td><strong>${p.name}</strong></td>
      <td>${categoryTag(p.category)}</td>
      <td class="${p.quantity < 5 ? 'qty-low' : 'qty-ok'}">${p.quantity}</td>
      <td>${rupee(p.pricePerUnit)}</td>
      <td>${rupee(p.quantity * p.pricePerUnit)}</td>
      <td style="display:flex;gap:6px;flex-wrap:wrap">${actionButtons(p)}</td>
    </tr>`).join('');
}

function filterProducts() {
  const q = document.getElementById('search-input').value.toLowerCase();
  renderProducts(allProducts.filter(p =>
    p.name.toLowerCase().includes(q) || p.category.toLowerCase().includes(q)));
}

function prevPage() { if (page > 0) loadProducts(page - 1); }
function nextPage() { if (page + 1 < totalPages) loadProducts(page + 1); }

async function addProduct() {
  const name     = document.getElementById('p-name').value.trim();
  const category = document.getElementById('p-category').value;
  const qty      = parseInt(document.getElementById('p-qty').value);
  const price    = parseFloat(document.getElementById('p-price').value);
  if (!name || !category || !qty || !price) { setMsg('form-msg', 'All fields are required.', true); return; }
  try {
    const res = await apiFetch(PRODUCT_API, {
      method: 'POST',
      body: JSON.stringify({ name, category, quantity: qty, pricePerUnit: price })
    });
    if (!res.ok) { setMsg('form-msg', 'Failed to add product.', true); return; }
    setMsg('form-msg', 'Product added / restocked!', false);
    ['p-name','p-category','p-qty','p-price'].forEach(id => document.getElementById(id).value = '');
    loadProducts();
  } catch (e) { setMsg('form-msg', 'Error: ' + e.message, true); }
}

// ── EDIT MODAL ────────────────────────────────────────
function openEditModal(id, name, category, qty, price) {
  document.getElementById('edit-id').value       = id;
  document.getElementById('edit-name').value     = name;
  document.getElementById('edit-category').value = category;
  document.getElementById('edit-qty').value      = qty;
  document.getElementById('edit-price').value    = price;
  document.getElementById('edit-msg').textContent = '';
  document.getElementById('edit-modal').style.display = 'flex';
}

function closeEditModal() {
  document.getElementById('edit-modal').style.display = 'none';
}

async function submitEdit() {
  const id       = document.getElementById('edit-id').value;
  const name     = document.getElementById('edit-name').value.trim();
  const category = document.getElementById('edit-category').value;
  const qty      = parseInt(document.getElementById('edit-qty').value);
  const price    = parseFloat(document.getElementById('edit-price').value);

  if (!name || !category || isNaN(qty) || isNaN(price)) {
    setMsg('edit-msg', 'All fields are required.', true); return;
  }

  try {
    const res = await apiFetch(`${PRODUCT_API}/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ name, category, quantity: qty, pricePerUnit: price })
    });
    if (!res.ok) { setMsg('edit-msg', 'Update failed.', true); return; }
    closeEditModal();
    loadProducts();
  } catch (e) { setMsg('edit-msg', 'Error: ' + e.message, true); }
}

// ── DELETE MODAL ──────────────────────────────────────
let pendingDeleteId = null;

function openDeleteModal(id) {
  pendingDeleteId = id;
  document.getElementById('delete-modal').style.display = 'flex';
}
function closeDeleteModal() {
  pendingDeleteId = null;
  document.getElementById('delete-modal').style.display = 'none';
}
async function confirmDelete() {
  if (!pendingDeleteId) return;
  try {
    await apiFetch(`${PRODUCT_API}/${pendingDeleteId}`, { method: 'DELETE' });
    closeDeleteModal();
    loadProducts();
  } catch (e) { alert('Delete failed.'); }
}

// ── LOW STOCK ─────────────────────────────────────────
async function loadLowStock() {
  const threshold = parseInt(document.getElementById('threshold').value) || 5;
  try {
    const res  = await apiFetch(`${PRODUCT_API}/low-stock?threshold=${threshold}`);
    const data = await res.json();
    const tbody = document.getElementById('lowstock-tbody');
    if (!data.length) {
      tbody.innerHTML = '<tr><td colspan="6"><div class="empty-state"><i class="fas fa-check-circle"></i><p>No low stock items</p></div></td></tr>';
    } else {
      tbody.innerHTML = data.map((p, i) => `
        <tr class="low-stock-row">
          <td>${i + 1}</td>
          <td><strong>${p.name}</strong></td>
          <td>${categoryTag(p.category)}</td>
          <td class="qty-low">${p.quantity}</td>
          <td>${rupee(p.pricePerUnit)}</td>
          <td style="display:flex;gap:6px">${actionButtons(p)}</td>
        </tr>`).join('');
    }
    const badge = document.getElementById('lowstock-badge');
    badge.textContent = data.length;
    badge.classList.toggle('hidden', data.length === 0);
  } catch (e) { console.error(e); }
}

// ── MANAGE USERS ──────────────────────────────────────
async function loadUsers() {
  if (!isAdmin) return;
  try {
    const res   = await fetch(AUTH_API + '/admin/users', { headers });
    const users = await res.json();
    const tbody = document.getElementById('users-tbody');
    if (!users.length) {
      tbody.innerHTML = '<tr><td colspan="5"><div class="empty-state"><i class="fas fa-users"></i><p>No users yet</p></div></td></tr>';
      return;
    }
    tbody.innerHTML = users.map((u, i) => `
      <tr>
        <td>${i + 1}</td>
        <td><strong>${u.username}</strong></td>
        <td>${u.email}</td>
        <td><span class="${roleTagClass(u.role)}">${roleLabel(u.role)}</span></td>
        <td style="display:flex;gap:6px;flex-wrap:wrap">
          <select id="role-select-${u.id}" style="padding:5px 8px;font-size:12px;border:1.5px solid #e2e8f0;border-radius:6px">
            <option value="ROLE_USER"  ${u.role==='ROLE_USER'  ? 'selected':''}>User</option>
            <option value="ROLE_ADMIN" ${u.role==='ROLE_ADMIN' ? 'selected':''}>Admin</option>
          </select>
          <button class="btn btn-sm btn-role"   onclick="changeRole(${u.id})"><i class="fas fa-check"></i></button>
          <button class="btn btn-sm btn-delete" onclick="deleteUser(${u.id})"><i class="fas fa-trash"></i></button>
        </td>
      </tr>`).join('');
  } catch (e) { console.error(e); }
}

async function createUser() {
  const username = document.getElementById('u-username').value.trim();
  const email    = document.getElementById('u-email').value.trim();
  const password = document.getElementById('u-password').value.trim();
  const role     = document.getElementById('u-role').value;
  if (!username || !email || !password) { setMsg('user-form-msg', 'All fields are required.', true); return; }
  try {
    const res = await fetch(AUTH_API + '/admin/users', {
      method: 'POST', headers,
      body: JSON.stringify({ username, email, password, role })
    });
    if (!res.ok) { const t = await res.text(); setMsg('user-form-msg', t || 'Failed.', true); return; }
    setMsg('user-form-msg', 'User created!', false);
    ['u-username','u-email','u-password'].forEach(id => document.getElementById(id).value = '');
    loadUsers();
  } catch (e) { setMsg('user-form-msg', 'Error: ' + e.message, true); }
}

async function deleteUser(id) {
  if (!confirm('Delete this user?')) return;
  await fetch(AUTH_API + '/admin/users/' + id, { method: 'DELETE', headers });
  loadUsers();
}

async function changeRole(id) {
  const newRole = document.getElementById('role-select-' + id).value;
  await fetch(AUTH_API + '/admin/users/' + id + '/role', {
    method: 'PATCH', headers, body: JSON.stringify({ role: newRole })
  });
  loadUsers();
}

// ── LOGOUT ────────────────────────────────────────────
function openLogoutModal()  { document.getElementById('logout-modal').style.display = 'flex'; }
function closeLogoutModal() { document.getElementById('logout-modal').style.display = 'none'; }
function confirmLogout() { localStorage.clear(); window.location.href = 'http://localhost:8081/'; }

// ── BOOT ──────────────────────────────────────────────
loadDashboard();
