# Coupons-Management-API

# 🛒 Coupons Management API – Monk Commerce 2025

## 📌 Objective

This project implements a **RESTful API** for managing and applying different types of discount coupons for an e-commerce platform.
Supported coupon types:

* **Cart-wise** – discounts on total cart value.
* **Product-wise** – discounts on specific products.
* **BxGy (Buy X Get Y)** – buy a certain quantity of items and get others for free/discounted.

The design is extensible so that new coupon types can be added in the future.

---

## 🚀 Features

* Create, fetch, and delete coupons.
* Fetch all applicable coupons for a given cart.
* Apply a specific coupon to a cart and return the updated cart with discounts.
* Designed with extensibility in mind.

---

## ⚙️ Endpoints

### Coupon CRUD

* `POST /api/coupons` → Create a new coupon
* `GET /api/coupons` → Get all coupons
* `GET /api/coupons/{id}` → Get coupon by ID
* `DELETE /api/coupons/{id}` → Delete coupon by ID
* `PUT /api/coupons/{id}` → Update coupon (❌ Not implemented, see limitations)

### Coupon Application

* `POST /api/coupons/applicable` → Fetch all applicable coupons for a given cart
* `POST /api/coupons/{id}/apply` → Apply a specific coupon to the cart

---

## 🧾 Example Payloads

### 1️⃣ Cart-wise Coupon

**POST /api/coupons**

```json
{
  "type": "CART_WISE",
  "isActive": true,
  "expiresAt": "2025-12-31T23:59:59",
  "cartWiseDetails": {
    "thresholdAmount": 100,
    "discountValue": 10,
    "discountType": "PERCENT"
  }
}
```

---

### 2️⃣ Product-wise Coupon

**POST /api/coupons**

```json
{
  "type": "PRODUCT_WISE",
  "isActive": true,
  "expiresAt": "2025-12-31T23:59:59",
  "productWiseDetails": {
    "productId": 1,
    "discountValue": 20,
    "discountType": "PERCENT"
  }
}
```

---

### 3️⃣ BxGy Coupon (Buy 3 of product 1 or 2, Get 1 product 3 Free)

**POST /api/coupons**

```json
{
  "type": "BXGY",
  "isActive": true,
  "expiresAt": "2025-12-31T23:59:59",
  "bxgyDetails": {
    "repetitionLimit": 2,
    "buyProducts": [
      { "productId": 1, "quantity": 3 },
      { "productId": 2, "quantity": 3 }
    ],
    "getProducts": [
      { "productId": 3, "quantity": 1, "discountType": "FREE", "discountValue": 0 }
    ]
  }
}
```

---

### 4️⃣ Get Applicable Coupons

**POST /api/coupons/applicable**

```json
{
  "items": [
    { "productId": 1, "quantity": 6, "price": 50 },
    { "productId": 2, "quantity": 3, "price": 30 },
    { "productId": 3, "quantity": 2, "price": 25 }
  ]
}
```

**Response:**

```json
[
  { "couponId": 1, "type": "cart-wise", "discount": 40 },
  { "couponId": 3, "type": "bxgy", "discount": 50 }
]
```

---

### 5️⃣ Apply Coupon

**POST /api/coupons/3/apply**

```json
{
  "items": [
    { "productId": 1, "quantity": 6, "price": 50 },
    { "productId": 2, "quantity": 3, "price": 30 },
    { "productId": 3, "quantity": 2, "price": 25 }
  ]
}
```

**Response:**

```json
{
  "updatedCart": {
    "items": [
      { "productId": 1, "quantity": 6, "price": 50, "totalDiscount": 0 },
      { "productId": 2, "quantity": 3, "price": 30, "totalDiscount": 0 },
      { "productId": 3, "quantity": 4, "price": 25, "totalDiscount": 50 }
    ],
    "totalPrice": 490,
    "totalDiscount": 50,
    "finalPrice": 440
  }
}
```

---

## ✅ Implemented Cases

### Cart-wise

* Percentage or fixed discount on cart total.
* Example: 10% off if cart total > ₹100.

### Product-wise

* Percentage or fixed discount on selected product.
* Example: 20% off on Product A.

### BxGy

* Buy a combination of products, get others for free/discounted.
* Supports:

  * **Repetition limit** (e.g., B2G1 can repeat 3 times).
  * **Multiple buy products** (X, Y, Z).
  * **Multiple get products** (A, B, C).
  * **Discount type per get product** (FREE, PERCENT, FIXED).
  * Adds free products to cart if not already present (for FREE type).

---

## ⚠️ Edge Cases Considered

* Not enough buy quantity → coupon not applied.
* Buy present but get not in cart → FREE product auto-added.
* Repetition limit capped.
* Fixed discount capped at product/cart total.

---

## 📌 Limitations

* **PUT /api/coupons/{id}** (update coupon) not implemented.
* Error handling: uses `RuntimeException`, no proper HTTP status codes.
* Multi-coupon stacking not supported (only one coupon applied at a time).
* Expiration date is stored but **not yet validated** during calculation.
* Cart prices assumed to be consistent (no historical price validation).

---

## 📌 Assumptions

* Only one coupon applied per request.
* Cart total = `sum(price * quantity)`.
* BXGY:

  * Free products added only if discountType = FREE.
  * Discount calculated only on products present in cart (except free).
* Coupons are assumed valid unless explicitly expired.

---

## 🔮 Future Improvements

* Add **multi-coupon stacking rules** (combine multiple coupons).
* Enforce **coupon expiry validation** during application.
* Add **better exception handling** (`@ControllerAdvice`, HTTP error codes).
* Implement **coupon update (PUT)** endpoint.
* Write **unit tests** for all coupon logics.

---

## 🛠️ Tech Stack

* Java 17
* Spring Boot
* Spring Data JPA
* H2/MySQL (configurable)
