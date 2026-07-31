// Install dependencies first:
// npm install express express-session body-parser

const express = require('express');
const session = require('express-session');
const bodyParser = require('body-parser');

const app = express();
const PORT = 3000;

// Middleware
app.use(bodyParser.urlencoded({ extended: true }));
app.use(session({
    secret: 'your-secret-key', // Change to a strong secret in production
    resave: false,
    saveUninitialized: false,
    cookie: { secure: false } // Set to true if using HTTPS
}));

// Dummy user for example
const USER = { username: 'admin', password: '1234' };

// Middleware to protect routes
function requireLogin(req, res, next) {
    if (req.session && req.session.user) {
        next(); // User is logged in
    } else {
        res.redirect('/login'); // Redirect if not logged in
    }
}

// Login page
app.get('/login', (req, res) => {
    res.send(`
        <form method="POST" action="/login">
            <input name="username" placeholder="Username" required />
            <input name="password" type="password" placeholder="Password" required />
            <button type="submit">Login</button>
        </form>
    `);
});

// Handle login
app.post('/login', (req, res) => {
    const { username, password } = req.body;
    if (username === USER.username && password === USER.password) {
        req.session.user = username;
        res.redirect('/dashboard');
    } else {
        res.send('Invalid credentials. <a href="/login">Try again</a>');
    }
});

// Protected page
app.get('/dashboard', requireLogin, (req, res) => {
    res.send(`Welcome ${req.session.user}! <a href="/logout">Logout</a>`);
});

// Logout
app.get('/logout', (req, res) => {
    req.session.destroy(() => {
        res.redirect('/login');
    });
});

app.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}`);
});
