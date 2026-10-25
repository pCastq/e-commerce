# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Status

The repository currently contains only `README.md` (the exam spec, in Italian) and `TSW_Progetto_Esame_Checklist.pdf`. No source code, build file, or tests exist yet, so there are no build/lint/test commands. Update this file once a build system (Maven/Gradle/IntelliJ artifact) is chosen.

## Project

University exam project (Tecnologie Software per il Web, a.a. 2025-2026): a complete e-commerce site with a customer area and an admin area. Group of up to 3; every member must be able to explain the whole project.

Stack (mandated): Java Servlet + JSP, Apache Tomcat (the site must run directly on Tomcat), MySQL/PostgreSQL via DataSource or DriverManager with a connection pool, HTML5/CSS3/JS, AJAX with Fetch API + JSON, MVC.

## Architecture rules from the spec (checklist items marked OBBLIGATORIO)

- **MVC with package separation**: Control package (servlets) and Model package (beans, Cart). DAO/DB access sits behind the model.
- **HTML only from JSP**, never printed from servlets. AJAX endpoints return JSON.
- **JSP fragments** (`include`) for header, footer, and navigation menu.
- **Servlet filters** for access control (admin area needs programmatic authentication) and other cross-cutting concerns.
- **Cart lives in the HTTP session**; confirming an order empties it.
- **Order lines must store price and VAT (IVA) at purchase time** (historical integrity), and deleting a product must not break past orders (referential integrity constraint, e.g. soft delete or non-cascading FK).
- **Security**: `PreparedStatement` for every query (never concatenate SQL); passwords stored hashed/encrypted.
- **Error pages** (404, 500, 403) configured in `web.xml`; the server must never lose control on errors.
- **Invoice** view styled via a print media query.

## Features to implement

- Customer: registration (regex validation + live AJAX check that the email isn't already registered), login/session, catalog with detail view, search bar with AJAX suggestions, cart (add/change quantity/remove), order confirmation, order history, success confirmation messages.
- Admin: product CRUD (delete requires confirmation), list of all orders, filter by date range and/or by customer (at least one filter required).

## Front-end rules

- Fully responsive (desktop, tablet, phone).
- Client-side JS validation of every form with regex before submit; inline error messages on submit (**no `alert`**); descriptive placeholders; focus on the active field. Submit only when all fields are valid.
