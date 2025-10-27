// app.js - Task Management App

// Data Models
class User {
    constructor(username, password, displayName, role) {
        this.id = Date.now().toString();
        this.username = username;
        this.password = password; // In real app, hash this
        this.displayName = displayName;
        this.role = role; // 'Staff' or 'Member'
    }
}

class Room {
    constructor(name, description, creatorId) {
        this.id = Date.now().toString();
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.members = [creatorId]; // Array of user IDs
        this.createdAt = new Date().toISOString();
    }
}

class Task {
    constructor(title, description, assigneeId, roomId, dueDate, creatorId) {
        this.id = Date.now().toString();
        this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
        this.roomId = roomId;
        this.dueDate = dueDate;
        this.creatorId = creatorId;
        this.createdAt = new Date().toISOString();
        this.status = 'OPEN'; // 'OPEN', 'IN_PROGRESS', 'COMPLETED'
        this.progressPercent = 0;
        this.history = []; // Array of {date, userId, percent, note}
    }
}

// Data Storage
const STORAGE_KEY = 'taskAppData';

let appData = {
    users: [],
    rooms: [],
    tasks: [],
    currentUser: null
};

// Initialize sample data
function initializeSampleData() {
    const sampleUsers = [
        new User('staff1', 'pass', 'Staff Leader', 'Staff'),
        new User('member1', 'pass', 'Member One', 'Member'),
        new User('member2', 'pass', 'Member Two', 'Member')
    ];

    const sampleRoom = new Room('Demo Project', 'A sample project room', sampleUsers[0].id);
    sampleRoom.members.push(sampleUsers[1].id, sampleUsers[2].id);

    const sampleTasks = [
        new Task('Design UI', 'Create wireframes and mockups', sampleUsers[1].id, sampleRoom.id, '2025-11-01', sampleUsers[0].id),
        new Task('Implement Backend', 'Build API endpoints', sampleUsers[2].id, sampleRoom.id, '2025-11-15', sampleUsers[0].id)
    ];

    appData.users = sampleUsers;
    appData.rooms = [sampleRoom];
    appData.tasks = sampleTasks;
    saveData();
}

// Load/Save Data
function loadData() {
    const data = localStorage.getItem(STORAGE_KEY);
    if (data) {
        appData = JSON.parse(data);
    } else {
        initializeSampleData();
    }
}

function saveData() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(appData));
}

// Auth Functions
function register(username, password, displayName, role) {
    if (appData.users.find(u => u.username === username)) {
        throw new Error('Username already exists');
    }
    const user = new User(username, password, displayName, role);
    appData.users.push(user);
    saveData();
    return user;
}

function login(username, password) {
    const user = appData.users.find(u => u.username === username && u.password === password);
    if (!user) {
        throw new Error('Invalid credentials');
    }
    appData.currentUser = user;
    saveData();
    return user;
}

function logout() {
    appData.currentUser = null;
    saveData();
    showView('login-view');
}

// UI Functions
function showView(viewId) {
    document.querySelectorAll('.view').forEach(view => view.classList.add('hidden'));
    document.getElementById(viewId).classList.remove('hidden');

    // Update navbar visibility
    const navbar = document.getElementById('navbar');
    const isLoggedIn = appData.currentUser !== null;
    // use flex when visible so layout rules apply
    navbar.style.display = isLoggedIn ? 'flex' : 'none';

    // Update UI based on role
    if (isLoggedIn) {
        updateUIBasedOnRole();
    }
    // update user display / logout visibility
    if (typeof updateNavbarForUser === 'function') updateNavbarForUser();
}

function updateUIBasedOnRole() {
    const isStaff = appData.currentUser.role === 'Staff';
    document.getElementById('create-room-btn').classList.toggle('hidden', !isStaff);
    document.getElementById('create-task-btn').classList.toggle('hidden', !isStaff);
}

function showToast(message, isError = false) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.classList.toggle('error', isError);
    toast.classList.remove('hidden');
    setTimeout(() => toast.classList.add('hidden'), 3000);
}

function showModal(content) {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modal-body');
    modalBody.innerHTML = content;
    modal.classList.remove('hidden');
}

function closeModal() {
    document.getElementById('modal').classList.add('hidden');
}

// Render Functions
function renderDashboard() {
    if (!appData.currentUser) return;

    if (appData.currentUser.role === 'Staff') {
        renderStaffDashboard();
    } else {
        renderMemberDashboard();
    }
}

function renderStaffDashboard() {
    const tasks = appData.tasks;
    const total = tasks.length;
    const open = tasks.filter(t => t.status === 'OPEN').length;
    const inProgress = tasks.filter(t => t.status === 'IN_PROGRESS').length;
    const completed = tasks.filter(t => t.status === 'COMPLETED').length;

    document.getElementById('total-tasks').textContent = total;
    document.getElementById('open-tasks').textContent = open;
    document.getElementById('in-progress-tasks').textContent = inProgress;
    document.getElementById('completed-tasks').textContent = completed;

    // Overdue tasks
    const today = new Date().toISOString().split('T')[0];
    const overdue = tasks.filter(t => t.dueDate < today && t.status !== 'COMPLETED');
    const overdueList = document.getElementById('overdue-list');
    overdueList.innerHTML = overdue.map(task => `<li>${task.title} - Due: ${task.dueDate}</li>`).join('');
}

function renderMemberDashboard() {
    const userTasks = appData.tasks.filter(t => t.assigneeId === appData.currentUser.id);
    const taskList = document.getElementById('member-task-list');
    taskList.innerHTML = userTasks.map(task => `
        <li>
            <h4>${task.title}</h4>
            <p>${task.description}</p>
            <div class="progress-bar">
                <div class="progress-fill" style="width: ${task.progressPercent}%"></div>
            </div>
            <p>Progress: ${task.progressPercent}%</p>
            <button onclick="updateTaskProgress('${task.id}')">Cập Nhật Tiến Độ</button>
            ${task.progressPercent === 100 ? '<button onclick="completeTask(\'' + task.id + '\')">Hoàn Thành</button>' : ''}
        </li>
    `).join('');
}

function renderRooms() {
    const rooms = appData.rooms.filter(room =>
        appData.currentUser.role === 'Staff' || room.members.includes(appData.currentUser.id)
    );
    const roomsList = document.getElementById('rooms-list');
    roomsList.innerHTML = rooms.map(room => `
        <li>
            <h4>${room.name}</h4>
            <p>${room.description}</p>
            <p>Members: ${room.members.length}</p>
            ${appData.currentUser.role === 'Staff' ? '<button onclick="editRoom(\'' + room.id + '\')">Chỉnh Sửa</button>' : ''}
        </li>
    `).join('');
}

function renderTasks() {
    let tasks;
    if (appData.currentUser.role === 'Staff') {
        tasks = appData.tasks;
    } else {
        tasks = appData.tasks.filter(t => t.assigneeId === appData.currentUser.id);
    }
    const tasksList = document.getElementById('tasks-list');
    tasksList.innerHTML = tasks.map(task => `
        <li>
            <h4>${task.title}</h4>
            <p>${task.description}</p>
            <p>Assignee: ${appData.users.find(u => u.id === task.assigneeId).displayName}</p>
            <p>Due: ${task.dueDate}</p>
            <div class="progress-bar">
                <div class="progress-fill" style="width: ${task.progressPercent}%"></div>
            </div>
            <p>Status: ${task.status} - Progress: ${task.progressPercent}%</p>
            ${appData.currentUser.role === 'Staff' ? `
                <button onclick="editTask('${task.id}')">Chỉnh Sửa</button>
                <button onclick="deleteTask('${task.id}')">Xóa</button>
            ` : ''}
            ${appData.currentUser.role === 'Member' && task.assigneeId === appData.currentUser.id ? `
                <button onclick="updateTaskProgress('${task.id}')">Cập Nhật Tiến Độ</button>
                ${task.progressPercent === 100 ? '<button onclick="completeTask(\'' + task.id + '\')">Hoàn Thành</button>' : ''}
            ` : ''}
        </li>
    `).join('');
}

// Event Handlers
document.addEventListener('DOMContentLoaded', () => {
    loadData();

    // Auth forms
    document.getElementById('login-form').addEventListener('submit', (e) => {
        e.preventDefault();
        const username = document.getElementById('login-username').value.trim();
        const password = document.getElementById('login-password').value;

        if (!username || !password) {
            showToast('Vui lòng nhập tên đăng nhập và mật khẩu', true);
            return;
        }

        try {
            login(username, password);
            showToast('Đăng nhập thành công');
            const dashboard = appData.currentUser.role === 'Staff' ? 'staff-dashboard' : 'member-dashboard';
            showView(dashboard);
            renderDashboard();
        } catch (error) {
            showToast(error.message, true);
        }
    });

    document.getElementById('register-form').addEventListener('submit', (e) => {
        e.preventDefault();
        const username = document.getElementById('reg-username').value.trim();
        const password = document.getElementById('reg-password').value;
        const displayName = document.getElementById('reg-displayname').value.trim();
        const role = document.getElementById('reg-role').value;

        if (!username || !password || !displayName || !role) {
            showToast('Vui lòng điền đầy đủ thông tin', true);
            return;
        }
        if (password.length < 4) {
            showToast('Mật khẩu phải có ít nhất 4 ký tự', true);
            return;
        }

        try {
            register(username, password, displayName, role);
            showToast('Đăng ký thành công');
            showView('login-view');
        } catch (error) {
            showToast(error.message, true);
        }
    });

    // Navigation
    document.getElementById('show-register').addEventListener('click', () => showView('register-view'));
    document.getElementById('show-login').addEventListener('click', () => showView('login-view'));
    document.getElementById('nav-home').addEventListener('click', () => {
        showView(appData.currentUser.role === 'Staff' ? 'staff-dashboard' : 'member-dashboard');
        renderDashboard();
    });
    document.getElementById('nav-rooms').addEventListener('click', () => {
        showView('rooms-view');
        renderRooms();
    });
    document.getElementById('nav-tasks').addEventListener('click', () => {
        showView('tasks-view');
        renderTasks();
    });
    // nav-logout is now a button in the right area
    const navLogoutBtn = document.getElementById('nav-logout');
    if (navLogoutBtn) navLogoutBtn.addEventListener('click', logout);

    // Modal close
    document.querySelector('.close').addEventListener('click', closeModal);

    // Help button
    document.getElementById('help-btn').addEventListener('click', () => {
        const content = `
            <h3>Hướng Dẫn Sử Dụng</h3>
            <p>Ứng dụng quản lý công việc hoạt động hoàn toàn offline, lưu trữ dữ liệu trong localStorage của trình duyệt.</p>
            <h4>Tài Khoản Mẫu:</h4>
            <ul>
                <li>Staff: username: staff1, password: pass</li>
                <li>Member: username: member1 hoặc member2, password: pass</li>
            </ul>
            <p>Đăng ký tài khoản mới hoặc sử dụng tài khoản mẫu để khám phá tính năng.</p>
        `;
        showModal(content);
    });

    // Initial view
    if (appData.currentUser) {
        showView(appData.currentUser.role === 'Staff' ? 'staff-dashboard' : 'member-dashboard');
        renderDashboard();
    } else {
        showView('login-view');
    }
});

// Update navbar user display and logout visibility when showing views
function updateNavbarForUser() {
    const navUser = document.getElementById('nav-user');
    const navLogout = document.getElementById('nav-logout');
    const navbar = document.getElementById('navbar');
    if (!appData.currentUser) {
        if (navUser) navUser.textContent = '';
        if (navLogout) navLogout.classList.add('hidden');
    } else {
        if (navUser) navUser.textContent = appData.currentUser.displayName || appData.currentUser.username;
        if (navLogout) navLogout.classList.remove('hidden');
    }
    // ensure navbar visibility
    if (navbar) navbar.style.display = appData.currentUser ? 'flex' : 'none';
}

// Additional functions for CRUD operations
function createRoom() {
    const content = `
        <h3>Tạo Phòng Mới</h3>
        <form id="create-room-form">
            <input type="text" id="room-name" placeholder="Tên phòng" required>
            <textarea id="room-description" placeholder="Mô tả" required></textarea>
            <button type="submit">Tạo</button>
        </form>
    `;
    showModal(content);

    document.getElementById('create-room-form').addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('room-name').value.trim();
        const description = document.getElementById('room-description').value.trim();

        if (!name || !description) {
            showToast('Vui lòng điền tên và mô tả phòng', true);
            return;
        }

        const room = new Room(name, description, appData.currentUser.id);
        appData.rooms.push(room);
        saveData();
        closeModal();
        showToast('Phòng đã được tạo');
        renderRooms();
    });
}

function editRoom(roomId) {
    const room = appData.rooms.find(r => r.id === roomId);
    if (!room) return;

    const content = `
        <h3>Chỉnh Sửa Phòng</h3>
        <form id="edit-room-form">
            <input type="text" id="room-name" value="${room.name}" required>
            <textarea id="room-description" required>${room.description}</textarea>
            <h4>Thêm Thành Viên</h4>
            <select id="add-member">
                <option value="">Chọn thành viên</option>
                ${appData.users.filter(u => u.role === 'Member' && !room.members.includes(u.id)).map(u => `<option value="${u.id}">${u.displayName}</option>`).join('')}
            </select>
            <button type="button" onclick="addMemberToRoom('${roomId}')">Thêm</button>
            <ul>${room.members.map(id => {
                const user = appData.users.find(u => u.id === id);
                return `<li>${user.displayName} ${id !== room.creatorId ? `<button onclick="removeMember('${roomId}', '${id}')">Xóa</button>` : ''}</li>`;
            }).join('')}</ul>
            <button type="submit">Lưu</button>
        </form>
    `;
    showModal(content);

    document.getElementById('edit-room-form').addEventListener('submit', (e) => {
        e.preventDefault();
        room.name = document.getElementById('room-name').value;
        room.description = document.getElementById('room-description').value;
        saveData();
        closeModal();
        showToast('Phòng đã được cập nhật');
        renderRooms();
    });
}

function addMemberToRoom(roomId) {
    const select = document.getElementById('add-member');
    const memberId = select.value;
    if (!memberId) return;

    const room = appData.rooms.find(r => r.id === roomId);
    if (!room.members.includes(memberId)) {
        room.members.push(memberId);
        saveData();
        showToast('Thành viên đã được thêm');
        editRoom(roomId); // Refresh modal
    }
}

function removeMember(roomId, memberId) {
    const room = appData.rooms.find(r => r.id === roomId);
    room.members = room.members.filter(id => id !== memberId);
    saveData();
    showToast('Thành viên đã được xóa');
    editRoom(roomId); // Refresh modal
}

function createTask() {
    const content = `
        <h3>Tạo Công Việc Mới</h3>
        <form id="create-task-form">
            <input type="text" id="task-title" placeholder="Tiêu đề" required>
            <textarea id="task-description" placeholder="Mô tả" required></textarea>
            <select id="task-room" required>
                <option value="">Chọn phòng</option>
                ${appData.rooms.filter(r => r.creatorId === appData.currentUser.id).map(r => `<option value="${r.id}">${r.name}</option>`).join('')}
            </select>
            <select id="task-assignee" required>
                <option value="">Chọn người thực hiện</option>
                ${appData.users.filter(u => u.role === 'Member').map(u => `<option value="${u.id}">${u.displayName}</option>`).join('')}
            </select>
            <input type="date" id="task-due-date" required>
            <button type="submit">Tạo</button>
        </form>
    `;
    showModal(content);

    document.getElementById('create-task-form').addEventListener('submit', (e) => {
        e.preventDefault();
        const title = document.getElementById('task-title').value.trim();
        const description = document.getElementById('task-description').value.trim();
        const roomId = document.getElementById('task-room').value;
        const assigneeId = document.getElementById('task-assignee').value;
        const dueDate = document.getElementById('task-due-date').value;

        if (!title || !description || !roomId || !assigneeId || !dueDate) {
            showToast('Vui lòng điền đầy đủ thông tin', true);
            return;
        }

        const today = new Date().toISOString().split('T')[0];
        if (dueDate < today) {
            showToast('Ngày hết hạn không được là quá khứ', true);
            return;
        }

        const task = new Task(title, description, assigneeId, roomId, dueDate, appData.currentUser.id);
        appData.tasks.push(task);
        saveData();
        closeModal();
        showToast('Công việc đã được tạo');
        renderTasks();
        renderDashboard();
    });
}

function editTask(taskId) {
    const task = appData.tasks.find(t => t.id === taskId);
    if (!task) return;

    const content = `
        <h3>Chỉnh Sửa Công Việc</h3>
        <form id="edit-task-form">
            <input type="text" id="task-title" value="${task.title}" required>
            <textarea id="task-description" required>${task.description}</textarea>
            <select id="task-assignee" required>
                <option value="">Chọn người thực hiện</option>
                ${appData.users.filter(u => u.role === 'Member').map(u => `<option value="${u.id}" ${u.id === task.assigneeId ? 'selected' : ''}>${u.displayName}</option>`).join('')}
            </select>
            <input type="date" id="task-due-date" value="${task.dueDate}" required>
            <button type="submit">Lưu</button>
        </form>
    `;
    showModal(content);

    document.getElementById('edit-task-form').addEventListener('submit', (e) => {
        e.preventDefault();
        task.title = document.getElementById('task-title').value;
        task.description = document.getElementById('task-description').value;
        task.assigneeId = document.getElementById('task-assignee').value;
        task.dueDate = document.getElementById('task-due-date').value;
        saveData();
        closeModal();
        showToast('Công việc đã được cập nhật');
        renderTasks();
        renderDashboard();
    });
}

function deleteTask(taskId) {
    const content = `
        <h3>Xác Nhận Xóa</h3>
        <p>Bạn có chắc muốn xóa công việc này?</p>
        <button onclick="confirmDelete('${taskId}')">Xóa</button>
        <button onclick="closeModal()">Hủy</button>
    `;
    showModal(content);
}

function confirmDelete(taskId) {
    appData.tasks = appData.tasks.filter(t => t.id !== taskId);
    saveData();
    closeModal();
    showToast('Công việc đã được xóa');
    renderTasks();
    renderDashboard();
}

function updateTaskProgress(taskId) {
    const task = appData.tasks.find(t => t.id === taskId);
    if (!task) return;

    const content = `
        <h3>Cập Nhật Tiến Độ</h3>
        <form id="update-progress-form">
            <label>Tiến Độ (%): <input type="number" id="progress-percent" min="0" max="100" value="${task.progressPercent}" required></label>
            <textarea id="progress-note" placeholder="Ghi chú"></textarea>
            <button type="submit">Cập Nhật</button>
        </form>
        <h4>Lịch Sử Báo Cáo</h4>
        <ul>${task.history.map(h => {
            const user = appData.users.find(u => u.id === h.userId);
            return `<li>${h.date}: ${user.displayName} - ${h.percent}% - ${h.note}</li>`;
        }).join('')}</ul>
    `;
    showModal(content);

    document.getElementById('update-progress-form').addEventListener('submit', (e) => {
        e.preventDefault();
        const percent = parseInt(document.getElementById('progress-percent').value);
        const note = document.getElementById('progress-note').value.trim();

        if (isNaN(percent) || percent < 0 || percent > 100) {
            showToast('Tiến độ phải từ 0 đến 100', true);
            return;
        }

        task.progressPercent = percent;
        task.status = percent === 100 ? 'COMPLETED' : percent > 0 ? 'IN_PROGRESS' : 'OPEN';
        task.history.push({
            date: new Date().toISOString(),
            userId: appData.currentUser.id,
            percent,
            note
        });
        saveData();
        closeModal();
        showToast('Tiến độ đã được cập nhật');
        renderTasks();
        renderDashboard();
    });
}

function completeTask(taskId) {
    const task = appData.tasks.find(t => t.id === taskId);
    if (task) {
        task.status = 'COMPLETED';
        saveData();
        showToast('Công việc đã hoàn thành');
        renderTasks();
        renderDashboard();
    }
}

function exportData() {
    const dataStr = JSON.stringify(appData, null, 2);
    const dataBlob = new Blob([dataStr], {type: 'application/json'});
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'task-app-data.json';
    link.click();
    URL.revokeObjectURL(url);
    showToast('Dữ liệu đã được xuất');
}