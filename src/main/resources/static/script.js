// // API Base URL
// const API_URL = '/api';
//
// // Global variables
// let currentUser = null;
// let authToken = null;
// let myIssuedBookIds = []; // Track user's issued books
// let allUsers = []; // Store all users for admin
//
// // Initialize
// document.addEventListener('DOMContentLoaded', function() {
//     checkAuth();
// });
//
// // Check if user is logged in
// function checkAuth() {
//     const token = localStorage.getItem('authToken');
//     const user = localStorage.getItem('currentUser');
//
//     if (token && user) {
//         authToken = token;
//         currentUser = JSON.parse(user);
//         showDashboard();
//     } else {
//         showSection('login');
//     }
// }
//
// // Show Section
// function showSection(sectionName) {
//     document.querySelectorAll('.section').forEach(section => {
//         section.classList.remove('active');
//     });
//
//     document.getElementById(sectionName + 'Section').classList.add('active');
// }
//
// // Handle Login
// async function handleLogin(event) {
//     event.preventDefault();
//
//     const username = document.getElementById('loginUsername').value;
//     const password = document.getElementById('loginPassword').value;
//
//     try {
//         const response = await fetch(`${API_URL}/auth/login`, {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify({ username, password })
//         });
//
//         const data = await response.json();
//
//         if (response.ok && data.token) {
//             authToken = data.token;
//             currentUser = { username: data.username, role: data.role };
//
//             localStorage.setItem('authToken', authToken);
//             localStorage.setItem('currentUser', JSON.stringify(currentUser));
//
//             showMessage('loginMessage', 'Login successful!', 'success');
//             setTimeout(() => {
//                 showDashboard();
//             }, 1000);
//         } else {
//             showMessage('loginMessage', data.message || 'Login failed', 'error');
//         }
//     } catch (error) {
//         showMessage('loginMessage', 'Connection error. Please try again.', 'error');
//     }
// }
//
// // Handle Register
// async function handleRegister(event) {
//     event.preventDefault();
//
//     const username = document.getElementById('regUsername').value;
//     const email = document.getElementById('regEmail').value;
//     const password = document.getElementById('regPassword').value;
//     const role = document.getElementById('regRole').value;
//
//     try {
//         const response = await fetch(`${API_URL}/auth/register`, {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify({ username, email, password, role })
//         });
//
//         const data = await response.json();
//
//         if (response.ok && data.token) {
//             authToken = data.token;
//             currentUser = { username: data.username, role: data.role };
//
//             localStorage.setItem('authToken', authToken);
//             localStorage.setItem('currentUser', JSON.stringify(currentUser));
//
//             showMessage('registerMessage', 'Registration successful!', 'success');
//             setTimeout(() => {
//                 showDashboard();
//             }, 1000);
//         } else {
//             showMessage('registerMessage', data.message || 'Registration failed', 'error');
//         }
//     } catch (error) {
//         showMessage('registerMessage', 'Connection error. Please try again.', 'error');
//     }
// }
//
// // Show Dashboard
// async function showDashboard() {
//     // Update navbar
//     document.getElementById('navLinks').style.display = 'none';
//     document.getElementById('navUser').style.display = 'flex';
//     document.getElementById('userInfo').textContent = `Welcome, ${currentUser.username} (${currentUser.role})`;
//
//     // Show admin controls if user is admin
//     if (currentUser.role === 'ADMIN') {
//         document.getElementById('adminControls').style.display = 'block';
//         await loadAllUsers(); // Load users for admin
//     } else {
//         document.getElementById('adminControls').style.display = 'none';
//     }
//
//     // Load user's issued books first
//     await loadMyIssuedBooks();
//
//     // Load all books
//     loadBooks();
//     showSection('dashboard');
// }
//
// // Load all users (Admin only)
// async function loadAllUsers() {
//     try {
//         const response = await fetch(`${API_URL}/admin/users`, {
//             headers: {
//                 'Authorization': `Bearer ${authToken}`
//             }
//         });
//
//         if (response.ok) {
//             allUsers = await response.json();
//         }
//     } catch (error) {
//         console.error('Error loading users:', error);
//     }
// }
//
// // Open Issue to User Modal
// function openIssueToUserModal(bookId, bookTitle) {
//     document.getElementById('issueBookId').value = bookId;
//     document.getElementById('issueBookTitle').value = bookTitle;
//
//     // Populate user dropdown
//     const userSelect = document.getElementById('issueUserId');
//     userSelect.innerHTML = '<option value="">Choose User</option>' +
//         allUsers.map(user => `<option value="${user.id}">${user.username} (${user.email})</option>`).join('');
//
//     document.getElementById('issueToUserModal').style.display = 'block';
// }
//
// // Close Issue to User Modal
// function closeIssueToUserModal() {
//     document.getElementById('issueToUserModal').style.display = 'none';
// }
//
// // Handle Issue to User
// async function handleIssueToUser(event) {
//     event.preventDefault();
//
//     const bookId = parseInt(document.getElementById('issueBookId').value);
//     const userId = parseInt(document.getElementById('issueUserId').value);
//
//     try {
//         const response = await fetch(`${API_URL}/admin/books/issue-to-user`, {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//                 'Authorization': `Bearer ${authToken}`
//             },
//             body: JSON.stringify({ bookId, userId })
//         });
//
//         const data = await response.json();
//
//         if (response.ok) {
//             alert(data.message);
//             closeIssueToUserModal();
//             await loadMyIssuedBooks();
//             loadBooks();
//         } else {
//             alert(data.message || 'Failed to issue book');
//         }
//     } catch (error) {
//         alert('Connection error');
//     }
// }
// // Load user's issued books
// async function loadMyIssuedBooks() {
//     try {
//         const response = await fetch(`${API_URL}/books/my-issued`, {
//             headers: {
//                 'Authorization': `Bearer ${authToken}`
//             }
//         });
//
//         if (response.ok) {
//             myIssuedBookIds = await response.json();
//         }
//     } catch (error) {
//         console.error('Error loading issued books:', error);
//     }
// }
//
// // Load Books
// async function loadBooks() {
//     try {
//         const response = await fetch(`${API_URL}/books`, {
//             headers: {
//                 'Authorization': `Bearer ${authToken}`
//             }
//         });
//
//         if (response.ok) {
//             const books = await response.json();
//             displayBooks(books);
//         } else {
//             showMessage('addBookMessage', 'Failed to load books', 'error');
//         }
//     } catch (error) {
//         console.error('Error loading books:', error);
//     }
// }
//
// // Display Books
// function displayBooks(books) {
//     const booksList = document.getElementById('booksList');
//
//     if (books.length === 0) {
//         booksList.innerHTML = '<p style="text-align: center; color: #666;">No books available</p>';
//         return;
//     }
//
//     booksList.innerHTML = books.map(book => {
//         const isIssuedByMe = myIssuedBookIds.includes(book.id);
//
//         return `
//         <div class="book-card">
//             <h4>${book.title}</h4>
//             <p><strong>Author:</strong> ${book.author}</p>
//             <p><strong>ISBN:</strong> ${book.isbn}</p>
//             <p><strong>Available:</strong> ${book.availableCopies} / ${book.totalCopies}</p>
//             ${book.description ? `<p>${book.description}</p>` : ''}
//             <span class="book-status ${book.available ? 'available' : 'unavailable'}">
//                 ${book.available ? '✓ Available' : '✗ Not Available'}
//             </span>
//             ${isIssuedByMe ? '<span class="book-status" style="background: #fef3c7; color: #92400e; margin-left: 0.5rem;">📖 Issued by You</span>' : ''}
//             <div class="book-actions">
//                 ${book.available && !isIssuedByMe && currentUser.role === 'USER' ? `
//                     <button class="btn btn-info" onclick="issueBook(${book.id})">
//                         📖 Get Book
//                     </button>
//                 ` : ''}
//                 ${book.available && currentUser.role === 'ADMIN' ? `
//                     <button class="btn btn-info" onclick="openIssueToUserModal(${book.id}, '${book.title.replace(/'/g, "\\'")}')">
//                         📖 Issue to User
//                     </button>
//                     <button class="btn btn-info" onclick="issueBook(${book.id})">
//                         📖 Issue to Self
//                     </button>
//                 ` : ''}
//                 ${isIssuedByMe ? `
//                     <button class="btn btn-warning" onclick="returnBook(${book.id})">
//                         🔙 Return Book
//                     </button>
//                 ` : ''}
//                 ${currentUser.role === 'ADMIN' ? `
//                     <button class="btn btn-warning" onclick="openEditModal(${book.id})">
//                         ✏️ Edit
//                     </button>
//                     <button class="btn btn-danger" onclick="deleteBook(${book.id})">
//                         🗑️ Delete
//                     </button>
//                 ` : ''}
//             </div>
//         </div>
//         `;
//     }).join('');
// }
//
// // Handle Add Book
// async function handleAddBook(event) {
//     event.preventDefault();
//
//     const bookData = {
//         title: document.getElementById('bookTitle').value,
//         author: document.getElementById('bookAuthor').value,
//         isbn: document.getElementById('bookIsbn').value,
//         totalCopies: parseInt(document.getElementById('bookCopies').value),
//         description: document.getElementById('bookDescription').value
//     };
//
//     try {
//         const response = await fetch(`${API_URL}/admin/books`, {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//                 'Authorization': `Bearer ${authToken}`
//             },
//             body: JSON.stringify(bookData)
//         });
//
//         if (response.ok) {
//             showMessage('addBookMessage', 'Book added successfully!', 'success');
//             document.getElementById('addBookForm').reset();
//             loadBooks();
//         } else {
//             showMessage('addBookMessage', 'Failed to add book', 'error');
//         }
//     } catch (error) {
//         showMessage('addBookMessage', 'Connection error', 'error');
//     }
// }
//
// // Issue Book
// async function issueBook(bookId) {
//     try {
//         const response = await fetch(`${API_URL}/books/issue`, {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//                 'Authorization': `Bearer ${authToken}`
//             },
//             body: JSON.stringify({ bookId })
//         });
//
//         const data = await response.json();
//
//         if (response.ok) {
//             alert(data.message);
//             await loadMyIssuedBooks(); // Refresh issued books
//             loadBooks();
//         } else {
//             alert(data.message || 'Failed to issue book');
//         }
//     } catch (error) {
//         alert('Connection error');
//     }
// }
//
// // Return Book
// async function returnBook(bookId) {
//     try {
//         const response = await fetch(`${API_URL}/books/return`, {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//                 'Authorization': `Bearer ${authToken}`
//             },
//             body: JSON.stringify({ bookId })
//         });
//
//         const data = await response.json();
//
//         if (response.ok) {
//             alert(data.message);
//             await loadMyIssuedBooks(); // Refresh issued books
//             loadBooks();
//         } else {
//             alert(data.message || 'Failed to return book');
//         }
//     } catch (error) {
//         alert('Connection error');
//     }
// }
//
// // Open Edit Modal
// async function openEditModal(bookId) {
//     try {
//         const response = await fetch(`${API_URL}/books/${bookId}`, {
//             headers: {
//                 'Authorization': `Bearer ${authToken}`
//             }
//         });
//
//         if (response.ok) {
//             const book = await response.json();
//
//             document.getElementById('editBookId').value = book.id;
//             document.getElementById('editBookTitle').value = book.title;
//             document.getElementById('editBookAuthor').value = book.author;
//             document.getElementById('editBookIsbn').value = book.isbn;
//             document.getElementById('editBookCopies').value = book.totalCopies;
//             document.getElementById('editBookDescription').value = book.description || '';
//
//             document.getElementById('editModal').style.display = 'block';
//         }
//     } catch (error) {
//         alert('Failed to load book details');
//     }
// }
//
// // Close Edit Modal
// function closeEditModal() {
//     document.getElementById('editModal').style.display = 'none';
// }
//
// // Handle Edit Book
// async function handleEditBook(event) {
//     event.preventDefault();
//
//     const bookId = document.getElementById('editBookId').value;
//     const bookData = {
//         title: document.getElementById('editBookTitle').value,
//         author: document.getElementById('editBookAuthor').value,
//         isbn: document.getElementById('editBookIsbn').value,
//         totalCopies: parseInt(document.getElementById('editBookCopies').value),
//         description: document.getElementById('editBookDescription').value
//     };
//
//     try {
//         const response = await fetch(`${API_URL}/admin/books/${bookId}`, {
//             method: 'PUT',
//             headers: {
//                 'Content-Type': 'application/json',
//                 'Authorization': `Bearer ${authToken}`
//             },
//             body: JSON.stringify(bookData)
//         });
//
//         if (response.ok) {
//             alert('Book updated successfully!');
//             closeEditModal();
//             loadBooks();
//         } else {
//             alert('Failed to update book');
//         }
//     } catch (error) {
//         alert('Connection error');
//     }
// }
//
// // Delete Book
// async function deleteBook(bookId) {
//     if (!confirm('Are you sure you want to delete this book?')) {
//         return;
//     }
//
//     try {
//         const response = await fetch(`${API_URL}/admin/books/${bookId}`, {
//             method: 'DELETE',
//             headers: {
//                 'Authorization': `Bearer ${authToken}`
//             }
//         });
//
//         const data = await response.json();
//
//         if (response.ok) {
//             alert(data.message);
//             loadBooks();
//         } else {
//             alert(data.message || 'Failed to delete book');
//         }
//     } catch (error) {
//         alert('Connection error');
//     }
// }
//
// // Logout
// function logout() {
//     localStorage.removeItem('authToken');
//     localStorage.removeItem('currentUser');
//     authToken = null;
//     currentUser = null;
//     myIssuedBookIds = [];
//
//     document.getElementById('navLinks').style.display = 'flex';
//     document.getElementById('navUser').style.display = 'none';
//
//     showSection('login');
// }
//
// // Show Message
// function showMessage(elementId, message, type) {
//     const messageElement = document.getElementById(elementId);
//     messageElement.textContent = message;
//     messageElement.className = `message ${type}`;
//     messageElement.style.display = 'block';
//
//     setTimeout(() => {
//         messageElement.style.display = 'none';
//     }, 5000);
// }
// // Handle Forgot Password
// async function handleForgotPassword(event) {
//     event.preventDefault();
//
//     const email = document.getElementById('forgotEmail').value;
//     const newPassword = document.getElementById('forgotNewPassword').value;
//     const confirmPassword = document.getElementById('forgotConfirmPassword').value;
//
//     if (newPassword !== confirmPassword) {
//         showMessage('forgotPasswordMessage', 'Passwords do not match!', 'error');
//         return;
//     }
//
//     if (newPassword.length < 6) {
//         showMessage('forgotPasswordMessage', 'Password must be at least 6 characters long', 'error');
//         return;
//     }
//
//     try {
//         const response = await fetch(`${API_URL}/auth/forgot-password`, {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify({ email, newPassword })
//         });
//
//         const data = await response.json();
//
//         if (response.ok) {
//             showMessage('forgotPasswordMessage', data.message, 'success');
//             document.getElementById('forgotPasswordForm').reset();
//             setTimeout(() => {
//                 showSection('login');
//             }, 2000);
//         } else {
//             showMessage('forgotPasswordMessage', data.message || 'Password reset failed', 'error');
//         }
//     } catch (error) {
//         showMessage('forgotPasswordMessage', 'Connection error. Please try again.', 'error');
//     }
// }
//
// // Close modal when clicking outside
// window.onclick = function(event) {
//     const editModal = document.getElementById('editModal');
//     const issueModal = document.getElementById('issueToUserModal');
//     const detailsModal = document.getElementById('bookDetailsModal');
//
//     if (event.target === editModal) {
//         closeEditModal();
//     }
//     if (event.target === issueModal) {
//         closeIssueToUserModal();
//     }
//     if (event.target === detailsModal) {
//         closeBookDetailsModal();
//     }
// }

// API Base URL
const API_URL = '/api';

// Global variables
let currentUser = null;
let authToken = null;
let myIssuedBookIds = []; // Track user's issued books
let allUsers = []; // Store all users for admin
let allBooksData = []; // Store all books for filtering

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    checkAuth();
});

// Check if user is logged in
function checkAuth() {
    const token = localStorage.getItem('authToken');
    const user = localStorage.getItem('currentUser');

    if (token && user) {
        authToken = token;
        currentUser = JSON.parse(user);
        showDashboard();
    } else {
        showSection('login');
    }
}

// Show Section
function showSection(sectionName) {
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });

    document.getElementById(sectionName + 'Section').classList.add('active');
}

// Handle Login
async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok && data.token) {
            authToken = data.token;
            currentUser = { username: data.username, role: data.role };

            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));

            showMessage('loginMessage', 'Login successful!', 'success');
            setTimeout(() => {
                showDashboard();
            }, 1000);
        } else {
            showMessage('loginMessage', data.message || 'Login failed', 'error');
        }
    } catch (error) {
        showMessage('loginMessage', 'Connection error. Please try again.', 'error');
    }
}

// Handle Register
async function handleRegister(event) {
    event.preventDefault();

    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const role = document.getElementById('regRole').value;

    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password, role })
        });

        const data = await response.json();

        if (response.ok && data.token) {
            authToken = data.token;
            currentUser = { username: data.username, role: data.role };

            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));

            showMessage('registerMessage', 'Registration successful!', 'success');
            setTimeout(() => {
                showDashboard();
            }, 1000);
        } else {
            showMessage('registerMessage', data.message || 'Registration failed', 'error');
        }
    } catch (error) {
        showMessage('registerMessage', 'Connection error. Please try again.', 'error');
    }
}

// Handle Forgot Password
async function handleForgotPassword(event) {
    event.preventDefault();

    const email = document.getElementById('forgotEmail').value;
    const newPassword = document.getElementById('forgotNewPassword').value;
    const confirmPassword = document.getElementById('forgotConfirmPassword').value;

    if (newPassword !== confirmPassword) {
        showMessage('forgotPasswordMessage', 'Passwords do not match!', 'error');
        return;
    }

    if (newPassword.length < 6) {
        showMessage('forgotPasswordMessage', 'Password must be at least 6 characters long', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/auth/forgot-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, newPassword })
        });

        const data = await response.json();

        if (response.ok) {
            showMessage('forgotPasswordMessage', data.message, 'success');
            document.getElementById('forgotPasswordForm').reset();
            setTimeout(() => {
                showSection('login');
            }, 2000);
        } else {
            showMessage('forgotPasswordMessage', data.message || 'Password reset failed', 'error');
        }
    } catch (error) {
        showMessage('forgotPasswordMessage', 'Connection error. Please try again.', 'error');
    }
}

// Show Dashboard
async function showDashboard() {
    // Update navbar
    document.getElementById('navLinks').style.display = 'none';
    document.getElementById('navUser').style.display = 'flex';
    document.getElementById('userInfo').textContent = `Welcome, ${currentUser.username} (${currentUser.role})`;

    // Show admin controls if user is admin
    if (currentUser.role === 'ADMIN') {
        document.getElementById('adminControls').style.display = 'block';
        await loadAllUsers(); // Load users for admin
    } else {
        document.getElementById('adminControls').style.display = 'none';
    }

    // Load user's issued books first
    await loadMyIssuedBooks();

    // Load all books
    loadBooks();
    showSection('dashboard');
}

// Load all users (Admin only)
async function loadAllUsers() {
    try {
        const response = await fetch(`${API_URL}/admin/users`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            allUsers = await response.json();
        }
    } catch (error) {
        console.error('Error loading users:', error);
    }
}

// Load user's issued books
async function loadMyIssuedBooks() {
    try {
        const response = await fetch(`${API_URL}/books/my-issued`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            myIssuedBookIds = await response.json();
        }
    } catch (error) {
        console.error('Error loading issued books:', error);
    }
}

// Load Books
async function loadBooks() {
    try {
        const response = await fetch(`${API_URL}/books`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const books = await response.json();
            allBooksData = books; // Store for filtering
            displayBooks(books);
        } else {
            showMessage('addBookMessage', 'Failed to load books', 'error');
        }
    } catch (error) {
        console.error('Error loading books:', error);
    }
}

// Filter Books (Search functionality)
function filterBooks() {
    const searchTerm = document.getElementById('bookSearch').value.toLowerCase();

    const filteredBooks = allBooksData.filter(book => {
        return book.title.toLowerCase().includes(searchTerm) ||
            book.author.toLowerCase().includes(searchTerm) ||
            book.isbn.toLowerCase().includes(searchTerm);
    });

    displayBooks(filteredBooks);
}

// Display Books (Compact View)
function displayBooks(books) {
    const booksList = document.getElementById('booksList');

    if (books.length === 0) {
        booksList.innerHTML = '<p style="text-align: center; color: #666;">No books available</p>';
        return;
    }

    booksList.innerHTML = books.map(book => {
        const isIssuedByMe = myIssuedBookIds.includes(book.id);

        return `
        <div class="book-card compact" onclick="openBookDetailsModal(${book.id})">
            <h4 class="book-title">${book.title}</h4>
            <p class="book-author"><strong>Author:</strong> ${book.author}</p>
            <span class="book-status ${book.available ? 'available' : 'unavailable'}">
                ${book.available ? '✓ Available' : '✗ Not Available'}
            </span>
            ${isIssuedByMe ? '<span class="book-status" style="background: #fef3c7; color: #92400e; margin-left: 0.5rem;">📖 Issued by You</span>' : ''}
            <p class="view-details">👁️ Click to view details</p>
        </div>
        `;
    }).join('');
}

// Open Book Details Modal
async function openBookDetailsModal(bookId) {
    try {
        const response = await fetch(`${API_URL}/books/${bookId}`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const book = await response.json();
            const isIssuedByMe = myIssuedBookIds.includes(book.id);

            const detailsContent = `
                <div class="book-details-full">
                    <h2>${book.title}</h2>
                    
                    <div class="detail-row">
                        <span class="detail-label">Author:</span>
                        <span class="detail-value">${book.author}</span>
                    </div>
                    
                    <div class="detail-row">
                        <span class="detail-label">ISBN:</span>
                        <span class="detail-value">${book.isbn}</span>
                    </div>
                    
                    <div class="detail-row">
                        <span class="detail-label">Total Copies:</span>
                        <span class="detail-value">${book.totalCopies}</span>
                    </div>
                    
                    <div class="detail-row">
                        <span class="detail-label">Available Copies:</span>
                        <span class="detail-value">${book.availableCopies}</span>
                    </div>
                    
                    <div class="detail-row">
                        <span class="detail-label">Status:</span>
                        <span class="book-status ${book.available ? 'available' : 'unavailable'}">
                            ${book.available ? '✓ Available' : '✗ Not Available'}
                        </span>
                        ${isIssuedByMe ? '<span class="book-status" style="background: #fef3c7; color: #92400e; margin-left: 0.5rem;">📖 Issued by You</span>' : ''}
                    </div>
                    
                    ${book.description ? `
                        <div class="description">
                            <strong>Description:</strong><br>
                            ${book.description}
                        </div>
                    ` : ''}
                    
                    <div class="book-actions" style="margin-top: 1.5rem;">
                        ${book.available && !isIssuedByMe && currentUser.role === 'USER' ? `
                            <button class="btn btn-info" onclick="issueBookFromModal(${book.id})">
                                📚 Get Book
                            </button>
                        ` : ''}
                        ${book.available && currentUser.role === 'ADMIN' ? `
                            <button class="btn btn-info" onclick="openIssueToUserModalFromDetails(${book.id}, '${book.title.replace(/'/g, "\\'")}')">
                                📖 Issue to User
                            </button>
                            <button class="btn btn-info" onclick="issueBookFromModal(${book.id})">
                                📖 Issue to Self
                            </button>
                        ` : ''}
                        ${isIssuedByMe ? `
                            <button class="btn btn-warning" onclick="returnBookFromModal(${book.id})">
                                🔙 Return Book
                            </button>
                        ` : ''}
                        ${currentUser.role === 'ADMIN' ? `
                            <button class="btn btn-warning" onclick="editBookFromModal(${book.id})">
                                ✏️ Edit
                            </button>
                            <button class="btn btn-danger" onclick="deleteBookFromModal(${book.id})">
                                🗑️ Delete
                            </button>
                        ` : ''}
                    </div>
                </div>
            `;

            document.getElementById('bookDetailsContent').innerHTML = detailsContent;
            document.getElementById('bookDetailsModal').style.display = 'block';
        }
    } catch (error) {
        alert('Failed to load book details');
    }
}

// Close Book Details Modal
function closeBookDetailsModal() {
    document.getElementById('bookDetailsModal').style.display = 'none';
}

// Helper functions to handle actions from modal
async function issueBookFromModal(bookId) {
    closeBookDetailsModal();
    await issueBook(bookId);
}

async function returnBookFromModal(bookId) {
    closeBookDetailsModal();
    await returnBook(bookId);
}

function editBookFromModal(bookId) {
    closeBookDetailsModal();
    openEditModal(bookId);
}

async function deleteBookFromModal(bookId) {
    closeBookDetailsModal();
    await deleteBook(bookId);
}

function openIssueToUserModalFromDetails(bookId, bookTitle) {
    closeBookDetailsModal();
    openIssueToUserModal(bookId, bookTitle);
}

// Open Issue to User Modal
function openIssueToUserModal(bookId, bookTitle) {
    document.getElementById('issueBookId').value = bookId;
    document.getElementById('issueBookTitle').value = bookTitle;

    // Populate user dropdown
    const userSelect = document.getElementById('issueUserId');
    userSelect.innerHTML = '<option value="">Choose User</option>' +
        allUsers.map(user => `<option value="${user.id}">${user.username} (${user.email})</option>`).join('');

    document.getElementById('issueToUserModal').style.display = 'block';
}

// Close Issue to User Modal
function closeIssueToUserModal() {
    document.getElementById('issueToUserModal').style.display = 'none';
}

// Handle Issue to User
async function handleIssueToUser(event) {
    event.preventDefault();

    const bookId = parseInt(document.getElementById('issueBookId').value);
    const userId = parseInt(document.getElementById('issueUserId').value);

    try {
        const response = await fetch(`${API_URL}/admin/books/issue-to-user`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ bookId, userId })
        });

        const data = await response.json();

        if (response.ok) {
            alert(data.message);
            closeIssueToUserModal();
            await loadMyIssuedBooks();
            loadBooks();
        } else {
            alert(data.message || 'Failed to issue book');
        }
    } catch (error) {
        alert('Connection error');
    }
}

// Handle Add Book
async function handleAddBook(event) {
    event.preventDefault();

    const bookData = {
        title: document.getElementById('bookTitle').value,
        author: document.getElementById('bookAuthor').value,
        isbn: document.getElementById('bookIsbn').value,
        totalCopies: parseInt(document.getElementById('bookCopies').value),
        description: document.getElementById('bookDescription').value
    };

    try {
        const response = await fetch(`${API_URL}/admin/books`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(bookData)
        });

        if (response.ok) {
            showMessage('addBookMessage', 'Book added successfully!', 'success');
            document.getElementById('addBookForm').reset();
            loadBooks();
        } else {
            showMessage('addBookMessage', 'Failed to add book', 'error');
        }
    } catch (error) {
        showMessage('addBookMessage', 'Connection error', 'error');
    }
}

// Issue Book
async function issueBook(bookId) {
    try {
        const response = await fetch(`${API_URL}/books/issue`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ bookId })
        });

        const data = await response.json();

        if (response.ok) {
            alert(data.message);
            await loadMyIssuedBooks(); // Refresh issued books
            loadBooks();
        } else {
            alert(data.message || 'Failed to issue book');
        }
    } catch (error) {
        alert('Connection error');
    }
}

// Return Book
async function returnBook(bookId) {
    try {
        const response = await fetch(`${API_URL}/books/return`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ bookId })
        });

        const data = await response.json();

        if (response.ok) {
            alert(data.message);
            await loadMyIssuedBooks(); // Refresh issued books
            loadBooks();
        } else {
            alert(data.message || 'Failed to return book');
        }
    } catch (error) {
        alert('Connection error');
    }
}

// Open Edit Modal
async function openEditModal(bookId) {
    try {
        const response = await fetch(`${API_URL}/books/${bookId}`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const book = await response.json();

            document.getElementById('editBookId').value = book.id;
            document.getElementById('editBookTitle').value = book.title;
            document.getElementById('editBookAuthor').value = book.author;
            document.getElementById('editBookIsbn').value = book.isbn;
            document.getElementById('editBookCopies').value = book.totalCopies;
            document.getElementById('editBookDescription').value = book.description || '';

            document.getElementById('editModal').style.display = 'block';
        }
    } catch (error) {
        alert('Failed to load book details');
    }
}

// Close Edit Modal
function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
}

// Handle Edit Book
async function handleEditBook(event) {
    event.preventDefault();

    const bookId = document.getElementById('editBookId').value;
    const bookData = {
        title: document.getElementById('editBookTitle').value,
        author: document.getElementById('editBookAuthor').value,
        isbn: document.getElementById('editBookIsbn').value,
        totalCopies: parseInt(document.getElementById('editBookCopies').value),
        description: document.getElementById('editBookDescription').value
    };

    try {
        const response = await fetch(`${API_URL}/admin/books/${bookId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(bookData)
        });

        if (response.ok) {
            alert('Book updated successfully!');
            closeEditModal();
            loadBooks();
        } else {
            alert('Failed to update book');
        }
    } catch (error) {
        alert('Connection error');
    }
}

// Delete Book
async function deleteBook(bookId) {
    if (!confirm('Are you sure you want to delete this book?')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/admin/books/${bookId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        const data = await response.json();

        if (response.ok) {
            alert(data.message);
            loadBooks();
        } else {
            alert(data.message || 'Failed to delete book');
        }
    } catch (error) {
        alert('Connection error');
    }
}

// Logout
function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    authToken = null;
    currentUser = null;
    myIssuedBookIds = [];
    allUsers = [];
    allBooksData = [];

    document.getElementById('navLinks').style.display = 'flex';
    document.getElementById('navUser').style.display = 'none';

    showSection('login');
}

// Show Message
function showMessage(elementId, message, type) {
    const messageElement = document.getElementById(elementId);
    messageElement.textContent = message;
    messageElement.className = `message ${type}`;
    messageElement.style.display = 'block';

    setTimeout(() => {
        messageElement.style.display = 'none';
    }, 5000);
}

// Close modal when clicking outside
window.onclick = function(event) {
    const editModal = document.getElementById('editModal');
    const issueModal = document.getElementById('issueToUserModal');
    const detailsModal = document.getElementById('bookDetailsModal');

    if (event.target === editModal) {
        closeEditModal();
    }
    if (event.target === issueModal) {
        closeIssueToUserModal();
    }
    if (event.target === detailsModal) {
        closeBookDetailsModal();
    }
}