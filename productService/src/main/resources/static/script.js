console.log('Script.js v2.1 loaded - Stats fixed!');
const apiBase = "http://localhost:8080/api/products";
let currentPage = 0;
let totalPages = 0;
const pageSize = 5;
// render helpers
function formatNumber(x) { return Number(x).toLocaleString('en-IN'); }

function getCategoryIcon(category) {
    const icons = {
        'Electronics': '📱',
        'Groceries': '🥗',
        'Clothing': '👕',
        'Stationery': '✏️',
        'Furniture': '🪑'
    };
    return icons[category] || '📦';
}

async function loadProducts(page = 0) {

    const res = await fetch(`${apiBase}/page?page=${page}&size=${pageSize}`);
    const data = await res.json();

    const products = data.content;

    currentPage = data.number;
    totalPages = data.totalPages;

    renderProducts(products);

    // correct total products
    document.getElementById("totalProducts").textContent = data.totalElements;

    // correct inventory value
    loadTotalValue();

    document.getElementById("pageInfo").innerText =
        `Page ${currentPage + 1} of ${totalPages}`;
}

function nextPage(){

    if(currentPage < totalPages - 1){
        loadProducts(currentPage + 1);
    }

}
function prevPage(){

    if(currentPage > 0){
        loadProducts(currentPage - 1);
    }

}
function renderProducts(products) {
    const tbody = document.querySelector("#productTable tbody");
    tbody.innerHTML = "";

    products.forEach((p, index) => {
        const total = (p.quantity * p.pricePerUnit) || 0;
        const row = document.createElement("tr");

        // highlight low stock (<5)
        const qtyClass = (p.quantity < 5) ? 'style="color: #f56565; font-weight: bold;"' : '';

        row.innerHTML = `
      <td>${index + 1}</td>
      <td><strong>${p.name}</strong></td>
      <td><span class="category-badge">${getCategoryIcon(p.category)} ${p.category}</span></td>
      <td ${qtyClass}>${p.quantity}</td>
      <td>₹${formatNumber(p.pricePerUnit)}</td>
      <td><strong>₹${formatNumber(total)}</strong></td>
      <td>
        <button class="action-btn edit-btn" onclick="showUpdateModal(${p.id}, \`${p.name}\`, \`${p.category}\`, ${p.quantity}, ${p.pricePerUnit})">
          <i class="fas fa-edit"></i> Edit
        </button>
                <button class="action-btn delete-btn" onclick="openDeleteModal(${p.id}, \`${p.name}\`)">
          <i class="fas fa-trash"></i> Delete
        </button>
      </td>
    `;
        tbody.appendChild(row);
    });
}

async function loadTotalValue(){

    const res = await fetch(`${apiBase}/value/total`);
    const data = await res.json();

    document.getElementById("totalValue").textContent =
        `₹${formatNumber(data.totalInventoryValue)}`;
}

// escape strings used in inline HTML attributes
function escapeHtml(text) {
    return text.replace(/'/g, "\\'").replace(/"/g, '\\"');
}

// Add product
document.getElementById("addProductForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const product = {
        name: document.getElementById("name").value.trim(),
        category: document.getElementById("category").value,
        quantity: parseInt(document.getElementById("quantity").value, 10),
        pricePerUnit: parseFloat(document.getElementById("price").value)
    };

    await fetch(apiBase, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(product)
    });

    e.target.reset();
    loadProducts();
});

// Filter by category
async function filterByCategory() {
    const selected = document.getElementById("categoryDropdown").value;
    if (!selected) { loadProducts(); return; }
    const res = await fetch(apiBase + '/category/' + encodeURIComponent(selected));
    const filtered = await res.json();
    renderProducts(filtered);
}

// Delete with confirmation modal
let pendingDeleteId = null;
function openDeleteModal(id, name) {
    pendingDeleteId = id;
    const nameSpan = document.getElementById('confirmProductName');
    if (nameSpan) nameSpan.textContent = name;
    const modal = document.getElementById('confirmModal');
    if (modal) modal.style.display = 'flex';
}

function closeDeleteModal() {
    const modal = document.getElementById('confirmModal');
    if (modal) modal.style.display = 'none';
    pendingDeleteId = null;
}

async function confirmDelete() {
    if (!pendingDeleteId) { closeDeleteModal(); return; }
    try {
        await fetch(`${apiBase}/${pendingDeleteId}`, { method: 'DELETE' });
    } catch (e) {
        console.error('Delete failed', e);
    }
    closeDeleteModal();
    loadProducts();
}

// Functions now handled by modal - see end of file

// Update
document.getElementById("updateProductForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("updateId").value;
    const updatedProduct = {
        name: document.getElementById("updateName").value.trim(),
        category: document.getElementById("updateCategory").value,
        quantity: parseInt(document.getElementById("updateQuantity").value, 10),
        pricePerUnit: parseFloat(document.getElementById("updatePrice").value)
    };

    await fetch(`${apiBase}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedProduct)
    });

    cancelUpdate();
    loadProducts();
});

// Logout function
// Logout modal controls
function openLogoutModal() {
    const modal = document.getElementById('logoutModal');
    if (modal) modal.style.display = 'flex';
}

function closeLogoutModal() {
    const modal = document.getElementById('logoutModal');
    if (modal) modal.style.display = 'none';
}

function confirmLogout() {
    // actual logout action
    localStorage.removeItem('sf_token');
    window.location.href = 'http://localhost:8081/';
}

// Update stats in dashboard
function updateStats(products) {
    console.log('Updating stats with products:', products);
    const totalProducts = products.length;
    const totalInventoryValue = products.reduce((sum, p) => sum + (p.quantity * p.pricePerUnit), 0);
    
    console.log('Total products:', totalProducts, 'Total inventory value:', totalInventoryValue);
    
    const totalProductsElement = document.getElementById('totalProducts');
    const totalValueElement = document.getElementById('totalValue');
    
    console.log('Elements found:', totalProductsElement, totalValueElement);
    
    if (totalProductsElement) {
        totalProductsElement.textContent = totalProducts;
    }
    
    if (totalValueElement) {
        totalValueElement.textContent = `₹${formatNumber(totalInventoryValue)}`;
    }
}

// Update modal functions
function showUpdateModal(id, name, category, quantity, price) {
    document.getElementById("updateId").value = id;
    document.getElementById("updateName").value = name;
    document.getElementById("updateCategory").value = category;
    document.getElementById("updateQuantity").value = quantity;
    document.getElementById("updatePrice").value = price;
    document.getElementById("updateModal").style.display = "flex";
}

function cancelUpdate() {
    document.getElementById("updateModal").style.display = "none";
}

// Initial load
loadProducts();
