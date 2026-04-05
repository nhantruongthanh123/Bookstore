# 🎨 Bookstore Frontend Architecture - Next.js

## 📋 Project Overview

**Application Name:** Bookstore Web Application  
**Framework:** Next.js 15 (App Router)  
**UI Library:** React 19  
**Language:** TypeScript  
**Styling:** Tailwind CSS + shadcn/ui  
**State Management:** Zustand / React Context  
**HTTP Client:** Axios  
**Form Handling:** React Hook Form + Zod  
**Authentication:** JWT (Access + Refresh Tokens)  
**Deployment:** Vercel / Docker  

---

## 🏗️ Project Structure

```
bookstore-frontend/
├── app/                          # Next.js App Router
│   ├── (auth)/                  # Auth route group
│   │   ├── login/
│   │   │   └── page.tsx         # Login page
│   │   ├── register/
│   │   │   └── page.tsx         # Registration page
│   │   └── oauth2/
│   │       └── redirect/
│   │           └── page.tsx     # OAuth2 callback handler
│   │
│   ├── (main)/                  # Main app route group (protected)
│   │   ├── layout.tsx           # Main layout with nav/sidebar
│   │   ├── page.tsx             # Homepage/Dashboard
│   │   ├── books/
│   │   │   ├── page.tsx         # Books listing with search
│   │   │   └── [id]/
│   │   │       └── page.tsx     # Book detail page
│   │   ├── categories/
│   │   │   ├── page.tsx         # Categories listing
│   │   │   └── [id]/
│   │   │       └── page.tsx     # Category books
│   │   ├── cart/
│   │   │   └── page.tsx         # Shopping cart
│   │   ├── orders/
│   │   │   ├── page.tsx         # Order history
│   │   │   └── [id]/
│   │   │       └── page.tsx     # Order detail
│   │   └── profile/
│   │       └── page.tsx         # User profile
│   │
│   ├── (admin)/                 # Admin route group (protected)
│   │   ├── layout.tsx           # Admin layout
│   │   ├── dashboard/
│   │   │   └── page.tsx         # Admin dashboard
│   │   ├── books/
│   │   │   ├── page.tsx         # Manage books
│   │   │   ├── create/
│   │   │   │   └── page.tsx     # Create book
│   │   │   └── [id]/
│   │   │       └── edit/
│   │   │           └── page.tsx # Edit book
│   │   ├── categories/
│   │   │   ├── page.tsx         # Manage categories
│   │   │   └── create/
│   │   │       └── page.tsx     # Create category
│   │   └── orders/
│   │       ├── page.tsx         # All orders
│   │       └── [id]/
│   │           └── page.tsx     # Order management
│   │
│   ├── layout.tsx               # Root layout
│   ├── error.tsx                # Error boundary
│   ├── loading.tsx              # Loading UI
│   └── not-found.tsx            # 404 page
│
├── components/                   # Reusable Components
│   ├── ui/                      # shadcn/ui components
│   │   ├── button.tsx
│   │   ├── card.tsx
│   │   ├── input.tsx
│   │   ├── dialog.tsx
│   │   ├── select.tsx
│   │   ├── table.tsx
│   │   └── ...
│   │
│   ├── layout/                  # Layout components
│   │   ├── Header.tsx
│   │   ├── Footer.tsx
│   │   ├── Sidebar.tsx
│   │   └── Navigation.tsx
│   │
│   ├── auth/                    # Authentication components
│   │   ├── LoginForm.tsx
│   │   ├── RegisterForm.tsx
│   │   ├── ProtectedRoute.tsx
│   │   └── OAuth2Button.tsx
│   │
│   ├── books/                   # Book components
│   │   ├── BookCard.tsx
│   │   ├── BookList.tsx
│   │   ├── BookDetail.tsx
│   │   ├── BookSearch.tsx
│   │   └── BookForm.tsx         # Admin form
│   │
│   ├── categories/              # Category components
│   │   ├── CategoryCard.tsx
│   │   ├── CategoryList.tsx
│   │   └── CategoryForm.tsx     # Admin form
│   │
│   ├── cart/                    # Cart components
│   │   ├── CartItem.tsx
│   │   ├── CartSummary.tsx
│   │   └── CartDrawer.tsx
│   │
│   ├── orders/                  # Order components
│   │   ├── OrderCard.tsx
│   │   ├── OrderList.tsx
│   │   ├── OrderDetail.tsx
│   │   └── OrderStatus.tsx
│   │
│   └── common/                  # Common components
│       ├── Pagination.tsx
│       ├── SearchBar.tsx
│       ├── Loader.tsx
│       ├── ErrorMessage.tsx
│       └── EmptyState.tsx
│
├── lib/                         # Core libraries and utilities
│   ├── api/                    # API integration
│   │   ├── client.ts           # Axios instance with interceptors
│   │   ├── endpoints.ts        # API endpoint constants
│   │   └── services/           # API service functions
│   │       ├── auth.service.ts
│   │       ├── book.service.ts
│   │       ├── category.service.ts
│   │       ├── cart.service.ts
│   │       ├── order.service.ts
│   │       └── file.service.ts
│   │
│   ├── hooks/                  # Custom React hooks
│   │   ├── useAuth.ts          # Authentication hook
│   │   ├── useBooks.ts         # Books data hook
│   │   ├── useCart.ts          # Cart management hook
│   │   ├── useDebounce.ts      # Debounce hook
│   │   └── usePagination.ts    # Pagination hook
│   │
│   ├── store/                  # State management (Zustand)
│   │   ├── authStore.ts        # Auth state
│   │   ├── cartStore.ts        # Cart state
│   │   └── uiStore.ts          # UI state (theme, modals)
│   │
│   ├── utils/                  # Utility functions
│   │   ├── format.ts           # Formatters (date, currency)
│   │   ├── validation.ts       # Validation helpers
│   │   ├── token.ts            # Token management
│   │   └── constants.ts        # App constants
│   │
│   └── schemas/                # Zod validation schemas
│       ├── auth.schema.ts
│       ├── book.schema.ts
│       ├── category.schema.ts
│       └── order.schema.ts
│
├── types/                       # TypeScript type definitions
│   ├── api.types.ts            # API request/response types
│   ├── auth.types.ts           # Auth types
│   ├── book.types.ts           # Book types
│   ├── category.types.ts       # Category types
│   ├── cart.types.ts           # Cart types
│   ├── order.types.ts          # Order types
│   └── common.types.ts         # Common types
│
├── public/                      # Static assets
│   ├── images/
│   ├── icons/
│   └── favicon.ico
│
├── styles/                      # Global styles
│   └── globals.css             # Tailwind + custom CSS
│
├── middleware.ts                # Next.js middleware (auth)
├── next.config.js              # Next.js configuration
├── tailwind.config.js          # Tailwind configuration
├── tsconfig.json               # TypeScript configuration
├── package.json                # Dependencies
└── .env.local                  # Environment variables
```

---

## 🔗 Backend API Integration

### API Client Configuration

**File:** `lib/api/client.ts`

```typescript
import axios from 'axios';
import { getAccessToken, getRefreshToken, setTokens, clearTokens } from '@/lib/utils/token';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// Create axios instance
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add access token
apiClient.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle token refresh
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If 401 and not already retried
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = getRefreshToken();
        if (!refreshToken) {
          clearTokens();
          window.location.href = '/login';
          return Promise.reject(error);
        }

        // Refresh token
        const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          refreshToken,
        });

        const { accessToken } = response.data;
        setTokens(accessToken, refreshToken);

        // Retry original request
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        clearTokens();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
```

---

## 🎯 API Service Layer

### Authentication Service

**File:** `lib/api/services/auth.service.ts`

```typescript
import apiClient from '../client';
import { 
  LoginRequest, 
  RegisterRequest, 
  AuthResponse, 
  TokenRefreshRequest,
  TokenRefreshResponse 
} from '@/types/auth.types';

export const authService = {
  // Register new user
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await apiClient.post('/auth/register', data);
    return response.data;
  },

  // Login with username/email and password
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post('/auth/login', data);
    return response.data;
  },

  // Refresh access token
  refreshToken: async (refreshToken: string): Promise<TokenRefreshResponse> => {
    const response = await apiClient.post('/auth/refresh', { refreshToken });
    return response.data;
  },

  // Google OAuth2 login
  googleLogin: () => {
    window.location.href = `${process.env.NEXT_PUBLIC_API_URL}/oauth2/authorization/google`;
  },
};
```

### Book Service

**File:** `lib/api/services/book.service.ts`

```typescript
import apiClient from '../client';
import { 
  BookResponse, 
  BookRequest, 
  SearchBookRequest,
  PageResponse 
} from '@/types';

export const bookService = {
  // Get all books with pagination
  getAllBooks: async (page = 0, size = 5): Promise<PageResponse<BookResponse>> => {
    const response = await apiClient.get('/books', {
      params: { page, size },
    });
    return response.data;
  },

  // Search books
  searchBooks: async (
    searchParams: SearchBookRequest,
    page = 0,
    size = 20
  ): Promise<PageResponse<BookResponse>> => {
    const response = await apiClient.get('/books/search', {
      params: { ...searchParams, page, size },
    });
    return response.data;
  },

  // Get book by ID
  getBookById: async (id: number): Promise<BookResponse> => {
    const response = await apiClient.get(`/books/${id}`);
    return response.data;
  },

  // Create book (ADMIN)
  createBook: async (data: BookRequest): Promise<BookResponse> => {
    const response = await apiClient.post('/books', data);
    return response.data;
  },

  // Update book (ADMIN)
  updateBook: async (id: number, data: BookRequest): Promise<BookResponse> => {
    const response = await apiClient.put(`/books/${id}`, data);
    return response.data;
  },

  // Delete book (ADMIN)
  deleteBook: async (id: number): Promise<void> => {
    await apiClient.delete(`/books/${id}`);
  },
};
```

### Category Service

**File:** `lib/api/services/category.service.ts`

```typescript
import apiClient from '../client';
import { CategoryResponse, CategoryRequest } from '@/types';

export const categoryService = {
  // Get all categories
  getAllCategories: async (): Promise<CategoryResponse[]> => {
    const response = await apiClient.get('/categories');
    return response.data;
  },

  // Get category by ID
  getCategoryById: async (id: number): Promise<CategoryResponse> => {
    const response = await apiClient.get(`/categories/${id}`);
    return response.data;
  },

  // Create category (ADMIN)
  createCategory: async (data: CategoryRequest): Promise<CategoryResponse> => {
    const response = await apiClient.post('/categories', data);
    return response.data;
  },

  // Update category (ADMIN)
  updateCategory: async (id: number, data: CategoryRequest): Promise<CategoryResponse> => {
    const response = await apiClient.put(`/categories/${id}`, data);
    return response.data;
  },

  // Delete category (ADMIN)
  deleteCategory: async (id: number): Promise<void> => {
    await apiClient.delete(`/categories/${id}`);
  },
};
```

### Cart Service

**File:** `lib/api/services/cart.service.ts`

```typescript
import apiClient from '../client';
import { CartResponse, AddToCartRequest, UpdateCartItemRequest } from '@/types';

export const cartService = {
  // Get user's cart
  getCart: async (): Promise<CartResponse> => {
    const response = await apiClient.get('/cart');
    return response.data;
  },

  // Add item to cart
  addToCart: async (data: AddToCartRequest): Promise<CartResponse> => {
    const response = await apiClient.post('/cart/add', data);
    return response.data;
  },

  // Update cart item quantity
  updateCartItem: async (
    itemId: number,
    data: UpdateCartItemRequest
  ): Promise<CartResponse> => {
    const response = await apiClient.put(`/cart/items/${itemId}`, data);
    return response.data;
  },

  // Remove item from cart
  removeCartItem: async (itemId: number): Promise<void> => {
    await apiClient.delete(`/cart/items/${itemId}`);
  },
};
```

### Order Service

**File:** `lib/api/services/order.service.ts`

```typescript
import apiClient from '../client';
import { OrderResponse, OrderRequest, PageResponse } from '@/types';

export const orderService = {
  // Place new order
  createOrder: async (data: OrderRequest): Promise<OrderResponse> => {
    const response = await apiClient.post('/orders', data);
    return response.data;
  },

  // Get user's order history
  getMyOrders: async (): Promise<OrderResponse[]> => {
    const response = await apiClient.get('/orders');
    return response.data;
  },

  // Get order by ID
  getOrderById: async (id: number): Promise<OrderResponse> => {
    const response = await apiClient.get(`/orders/${id}`);
    return response.data;
  },

  // Cancel order
  cancelOrder: async (id: number): Promise<OrderResponse> => {
    const response = await apiClient.patch(`/orders/${id}/cancel`);
    return response.data;
  },

  // ADMIN: Get all orders
  getAllOrders: async (page = 0, size = 10): Promise<PageResponse<OrderResponse>> => {
    const response = await apiClient.get('/orders/admin', {
      params: { page, size },
    });
    return response.data;
  },

  // ADMIN: Update order status
  updateOrderStatus: async (id: number, status: string): Promise<OrderResponse> => {
    const response = await apiClient.patch(`/orders/admin/${id}/status`, null, {
      params: { status },
    });
    return response.data;
  },
};
```

### File Upload Service

**File:** `lib/api/services/file.service.ts`

```typescript
import apiClient from '../client';

export const fileService = {
  // Upload image to Cloudinary
  uploadImage: async (file: File): Promise<string> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data.url;
  },
};
```

---

## 📘 TypeScript Type Definitions

### Authentication Types

**File:** `types/auth.types.ts`

```typescript
export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  phoneNumber?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: UserResponse;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  fullName: string;
  phoneNumber?: string;
  roles: string[];
}

export interface TokenRefreshRequest {
  refreshToken: string;
}

export interface TokenRefreshResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}
```

### Book Types

**File:** `types/book.types.ts`

```typescript
export interface BookResponse {
  id: number;
  title: string;
  author: string;
  publisher: string;
  price: number;
  isbn: string;
  description: string;
  coverImage: string;
  quantity: number;
  categories: CategoryResponse[];
}

export interface BookRequest {
  title: string;
  author: string;
  publisher: string;
  price: number;
  isbn: string;
  description: string;
  coverImage: string;
  quantity: number;
  categoryIds: number[];
}

export interface SearchBookRequest {
  title?: string;
  author?: string;
  category?: string;
  minPrice?: number;
  maxPrice?: number;
}
```

### Category Types

**File:** `types/category.types.ts`

```typescript
export interface CategoryResponse {
  id: number;
  name: string;
  description: string;
}

export interface CategoryRequest {
  name: string;
  description: string;
}
```

### Cart Types

**File:** `types/cart.types.ts`

```typescript
export interface CartResponse {
  id: number;
  userId: number;
  items: CartItemResponse[];
  totalPrice: number;
  totalItems: number;
}

export interface CartItemResponse {
  id: number;
  bookId: number;
  bookTitle: string;
  bookPrice: number;
  bookCoverImage: string;
  quantity: number;
  subtotal: number;
}

export interface AddToCartRequest {
  bookId: number;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}
```

### Order Types

**File:** `types/order.types.ts`

```typescript
export interface OrderResponse {
  id: number;
  userId: number;
  orderDate: string;
  totalAmount: number;
  status: OrderStatus;
  shippingAddress: string;
  phoneNumber: string;
  items: OrderItemResponse[];
}

export interface OrderItemResponse {
  id: number;
  bookId: number;
  bookTitle: string;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface OrderRequest {
  shippingAddress: string;
  phoneNumber: string;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  SHIPPING = 'SHIPPING',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
}
```

### Common Types

**File:** `types/common.types.ts`

```typescript
export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
}

export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  timestamp: string;
}
```

---

## 🔐 Authentication Implementation

### Auth Store (Zustand)

**File:** `lib/store/authStore.ts`

```typescript
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { UserResponse } from '@/types';

interface AuthState {
  user: UserResponse | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  
  setAuth: (user: UserResponse, accessToken: string, refreshToken: string) => void;
  clearAuth: () => void;
  updateTokens: (accessToken: string, refreshToken: string) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isAdmin: false,

      setAuth: (user, accessToken, refreshToken) => {
        set({
          user,
          accessToken,
          refreshToken,
          isAuthenticated: true,
          isAdmin: user.roles.includes('ROLE_ADMIN'),
        });
      },

      clearAuth: () => {
        set({
          user: null,
          accessToken: null,
          refreshToken: null,
          isAuthenticated: false,
          isAdmin: false,
        });
      },

      updateTokens: (accessToken, refreshToken) => {
        set({ accessToken, refreshToken });
      },
    }),
    {
      name: 'auth-storage',
    }
  )
);
```

### Protected Route Component

**File:** `components/auth/ProtectedRoute.tsx`

```typescript
'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/lib/store/authStore';
import { Loader } from '@/components/common/Loader';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireAdmin?: boolean;
}

export function ProtectedRoute({ children, requireAdmin = false }: ProtectedRouteProps) {
  const router = useRouter();
  const { isAuthenticated, isAdmin } = useAuthStore();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (requireAdmin && !isAdmin) {
      router.push('/');
      return;
    }

    setIsLoading(false);
  }, [isAuthenticated, isAdmin, requireAdmin, router]);

  if (isLoading) {
    return <Loader />;
  }

  return <>{children}</>;
}
```

### OAuth2 Redirect Handler

**File:** `app/(auth)/oauth2/redirect/page.tsx`

```typescript
'use client';

import { useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useAuthStore } from '@/lib/store/authStore';

export default function OAuth2RedirectPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { setAuth } = useAuthStore();

  useEffect(() => {
    const token = searchParams.get('token');
    const error = searchParams.get('error');

    if (error) {
      console.error('OAuth2 error:', error);
      router.push('/login?error=oauth_failed');
      return;
    }

    if (token) {
      // Parse JWT to get user info (or make API call)
      // For now, assuming backend sends user data in token
      // You might need to call /api/auth/me to get user details
      
      // Simplified example - adjust based on your backend response
      const [accessToken, refreshToken] = token.split('.');
      
      // Call API to get user info
      authService.getCurrentUser(accessToken).then((user) => {
        setAuth(user, accessToken, refreshToken);
        router.push('/');
      });
    } else {
      router.push('/login');
    }
  }, [searchParams, setAuth, router]);

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center">
        <h2 className="text-2xl font-semibold mb-4">Completing login...</h2>
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
      </div>
    </div>
  );
}
```

---

## 🛒 Cart Management with Zustand

**File:** `lib/store/cartStore.ts`

```typescript
import { create } from 'zustand';
import { cartService } from '@/lib/api/services/cart.service';
import { CartResponse, AddToCartRequest } from '@/types';

interface CartState {
  cart: CartResponse | null;
  isLoading: boolean;
  error: string | null;
  
  fetchCart: () => Promise<void>;
  addToCart: (data: AddToCartRequest) => Promise<void>;
  updateQuantity: (itemId: number, quantity: number) => Promise<void>;
  removeItem: (itemId: number) => Promise<void>;
  clearCart: () => void;
}

export const useCartStore = create<CartState>((set, get) => ({
  cart: null,
  isLoading: false,
  error: null,

  fetchCart: async () => {
    set({ isLoading: true, error: null });
    try {
      const cart = await cartService.getCart();
      set({ cart, isLoading: false });
    } catch (error: any) {
      set({ error: error.message, isLoading: false });
    }
  },

  addToCart: async (data) => {
    set({ isLoading: true, error: null });
    try {
      const cart = await cartService.addToCart(data);
      set({ cart, isLoading: false });
    } catch (error: any) {
      set({ error: error.message, isLoading: false });
      throw error;
    }
  },

  updateQuantity: async (itemId, quantity) => {
    set({ isLoading: true, error: null });
    try {
      const cart = await cartService.updateCartItem(itemId, { quantity });
      set({ cart, isLoading: false });
    } catch (error: any) {
      set({ error: error.message, isLoading: false });
    }
  },

  removeItem: async (itemId) => {
    set({ isLoading: true, error: null });
    try {
      await cartService.removeCartItem(itemId);
      await get().fetchCart();
    } catch (error: any) {
      set({ error: error.message, isLoading: false });
    }
  },

  clearCart: () => {
    set({ cart: null });
  },
}));
```

---

## 📄 Example Pages

### Login Page

**File:** `app/(auth)/login/page.tsx`

```typescript
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { authService } from '@/lib/api/services/auth.service';
import { useAuthStore } from '@/lib/store/authStore';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

const loginSchema = z.object({
  usernameOrEmail: z.string().min(1, 'Username or email is required'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function LoginPage() {
  const router = useRouter();
  const { setAuth } = useAuthStore();
  const [error, setError] = useState('');

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      const response = await authService.login(data);
      setAuth(response.user, response.accessToken, response.refreshToken);
      router.push('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed');
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
        <h2 className="text-3xl font-bold text-center">Login</h2>
        
        {error && (
          <div className="p-3 text-sm text-red-600 bg-red-50 rounded">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <Input
              {...register('usernameOrEmail')}
              placeholder="Username or Email"
              error={errors.usernameOrEmail?.message}
            />
          </div>

          <div>
            <Input
              {...register('password')}
              type="password"
              placeholder="Password"
              error={errors.password?.message}
            />
          </div>

          <Button type="submit" className="w-full" disabled={isSubmitting}>
            {isSubmitting ? 'Logging in...' : 'Login'}
          </Button>
        </form>

        <div className="relative">
          <div className="absolute inset-0 flex items-center">
            <span className="w-full border-t" />
          </div>
          <div className="relative flex justify-center text-xs uppercase">
            <span className="bg-white px-2 text-gray-500">Or continue with</span>
          </div>
        </div>

        <Button
          variant="outline"
          className="w-full"
          onClick={() => authService.googleLogin()}
        >
          <svg className="w-5 h-5 mr-2" viewBox="0 0 24 24">
            {/* Google icon SVG */}
          </svg>
          Google
        </Button>

        <p className="text-center text-sm text-gray-600">
          Don't have an account?{' '}
          <a href="/register" className="text-blue-600 hover:underline">
            Register
          </a>
        </p>
      </div>
    </div>
  );
}
```

### Books Listing Page with Search

**File:** `app/(main)/books/page.tsx`

```typescript
'use client';

import { useState, useEffect } from 'react';
import { useSearchParams } from 'next/navigation';
import { bookService } from '@/lib/api/services/book.service';
import { BookCard } from '@/components/books/BookCard';
import { BookSearch } from '@/components/books/BookSearch';
import { Pagination } from '@/components/common/Pagination';
import { Loader } from '@/components/common/Loader';
import { BookResponse, SearchBookRequest } from '@/types';

export default function BooksPage() {
  const searchParams = useSearchParams();
  const [books, setBooks] = useState<BookResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
    totalPages: 0,
    totalElements: 0,
  });

  const fetchBooks = async (searchData?: SearchBookRequest, page = 0) => {
    setLoading(true);
    try {
      const response = searchData
        ? await bookService.searchBooks(searchData, page, 20)
        : await bookService.getAllBooks(page, 20);

      setBooks(response.content);
      setPagination({
        page: response.number,
        size: response.size,
        totalPages: response.totalPages,
        totalElements: response.totalElements,
      });
    } catch (error) {
      console.error('Failed to fetch books:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBooks();
  }, []);

  const handleSearch = (searchData: SearchBookRequest) => {
    fetchBooks(searchData, 0);
  };

  const handlePageChange = (page: number) => {
    fetchBooks(undefined, page);
  };

  if (loading) return <Loader />;

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-4xl font-bold mb-8">Books</h1>
      
      <BookSearch onSearch={handleSearch} />

      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6 mt-8">
        {books.map((book) => (
          <BookCard key={book.id} book={book} />
        ))}
      </div>

      {books.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500">No books found</p>
        </div>
      )}

      <Pagination
        currentPage={pagination.page}
        totalPages={pagination.totalPages}
        onPageChange={handlePageChange}
      />
    </div>
  );
}
```

---

## 🎨 Environment Variables

**File:** `.env.local`

```bash
# Backend API URL
NEXT_PUBLIC_API_URL=http://localhost:8080/api

# OAuth2 Configuration
NEXT_PUBLIC_GOOGLE_CLIENT_ID=your_google_client_id

# App Configuration
NEXT_PUBLIC_APP_NAME=Bookstore
NEXT_PUBLIC_APP_URL=http://localhost:3000

# Analytics (optional)
NEXT_PUBLIC_GA_ID=your_google_analytics_id
```

---

## 📦 Package Dependencies

**File:** `package.json`

```json
{
  "name": "bookstore-frontend",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint"
  },
  "dependencies": {
    "next": "^15.0.0",
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "axios": "^1.7.0",
    "zustand": "^5.0.0",
    "react-hook-form": "^7.53.0",
    "zod": "^3.23.0",
    "@hookform/resolvers": "^3.9.0",
    "tailwindcss": "^3.4.0",
    "class-variance-authority": "^0.7.0",
    "clsx": "^2.1.0",
    "tailwind-merge": "^2.5.0",
    "lucide-react": "^0.453.0",
    "@radix-ui/react-dialog": "^1.1.0",
    "@radix-ui/react-dropdown-menu": "^2.1.0",
    "@radix-ui/react-select": "^2.1.0",
    "@radix-ui/react-slot": "^1.1.0"
  },
  "devDependencies": {
    "typescript": "^5.6.0",
    "@types/node": "^22.0.0",
    "@types/react": "^19.0.0",
    "@types/react-dom": "^19.0.0",
    "postcss": "^8.4.0",
    "autoprefixer": "^10.4.0",
    "eslint": "^9.0.0",
    "eslint-config-next": "^15.0.0"
  }
}
```

---

## 🚀 Getting Started

### Installation

```bash
# Create Next.js app
npx create-next-app@latest bookstore-frontend --typescript --tailwind --app

# Navigate to project
cd bookstore-frontend

# Install dependencies
npm install axios zustand react-hook-form zod @hookform/resolvers

# Install shadcn/ui
npx shadcn@latest init

# Add shadcn components
npx shadcn@latest add button input card dialog select table
```

### Running the Application

```bash
# Development mode
npm run dev

# Production build
npm run build
npm start
```

---

## 🎯 Key Features

✅ **Next.js 15 App Router** - Modern routing with layouts  
✅ **TypeScript** - Type-safe development  
✅ **Tailwind CSS + shadcn/ui** - Beautiful, accessible components  
✅ **Zustand** - Lightweight state management  
✅ **React Hook Form + Zod** - Form handling with validation  
✅ **Axios Interceptors** - Automatic token refresh  
✅ **Protected Routes** - Authentication middleware  
✅ **OAuth2 Integration** - Google login support  
✅ **Responsive Design** - Mobile-first approach  
✅ **Server Components** - Optimized performance  
✅ **Client Components** - Interactive UI  

---

## 📊 Integration Flow

```
User Action → React Component → API Service → Axios Client → Backend API
                                                    ↓
                                            Interceptor (Add Token)
                                                    ↓
                                            Backend Validation
                                                    ↓
                                            Response/Error
                                                    ↓
                                        Interceptor (Refresh Token)
                                                    ↓
                                            Update State (Zustand)
                                                    ↓
                                            UI Update
```

---

*Frontend Architecture for Bookstore Application - Aligned with Spring Boot Backend*
