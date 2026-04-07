import React, { useState, useEffect } from "react";

import bg1 from "./assets/login.png";
import bg2 from "./assets/login1.png";
import bg3 from "./assets/login2.png";
import Bg from "./assets/bg.png";
import Bg1 from "./assets/bg1.png";

const images = [bg1, bg2, bg3];

const booksData = Array.from({ length: 50 }, (_, i) => ({
  id: i,
  isbn: `BK-${1000 + i}`,
  name: `Book ${i + 1}`,
  category: ["Kinh dị", "Kì bí"],
  author: "Mr Bean",
  publisher: "Rowan Atkinson",
  price: 10 + i,
  stock: 5 + (i % 10),
}));

export default function UserLoginDashboard() {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [search, setSearch] = useState("");
  const [selectedBook, setSelectedBook] = useState(null);
  const [cart, setCart] = useState([]);
  const [view, setView] = useState("dashboard"); 
  const [quantityModal, setQuantityModal] = useState(null);
  const [page, setPage] = useState(1);
  const perPage = 8;

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % images.length);
    }, 5000);
    return () => clearInterval(timer);
  }, []);

  const filtered = booksData.filter(
    (b) =>
      b.name.toLowerCase().includes(search.toLowerCase()) ||
      b.isbn.toLowerCase().includes(search.toLowerCase()) ||
      b.category.some(cat => cat.toLowerCase().includes(search.toLowerCase()))
  );

  const totalPages = Math.ceil(filtered.length / perPage);
  const display = filtered.slice((page - 1) * perPage, page * perPage);

  // Logic handle cart add
  const handleAddToCart = (book, qty) => {
    setCart(prev => {
      const existing = prev.find(item => item.id === book.id);
      if (existing) {
        return prev.map(item => 
          item.id === book.id ? { ...item, quantity: item.quantity + qty } : item
        );
      }
      return [...prev, { ...book, quantity: qty }];
    });
    setQuantityModal(null);
    setSelectedBook(null);
  };

  if (view === "cart") {
    return <CartPage cart={cart} setCart={setCart} goBack={() => setView("dashboard")} />;
  }

  return (
    <div className="h-screen w-full relative overflow-hidden font-sans">
      
      {/* Background Slideshow */}
      <div className="absolute w-full h-full overflow-hidden bg-gray-200 z-0">
        {images.map((img, index) => (
          <img
            key={index}
            src={img}
            className={`absolute inset-0 w-full h-full object-cover transition-opacity duration-[2000ms] ease-in-out ${
              index === currentIndex ? 'opacity-100 z-10' : 'opacity-0 z-0'
            }`}
          />
        ))}
      </div>

      <div className="absolute inset-0 bg-white/40 backdrop-blur-sm z-10"></div>

      {/* Header UI */}
      <div className="absolute top-6 left-0 w-full flex justify-between px-10 z-50">
        <div className="flex-1"></div>
        <input
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(1); }}
          placeholder="Tìm kiếm sách hoặc ISBN..."
          className="w-[40%] p-3 rounded-full bg-white shadow-xl text-center outline-none border border-gray-200 focus:ring-2 focus:ring-purple-400"
        />
        <div className="flex-1 flex justify-end">
            <button
                onClick={() => setView("cart")}
                className="bg-purple-600 text-white px-6 py-2 rounded-full shadow-lg hover:bg-purple-700 transition font-bold"
            >
                Giỏ hàng ({cart.length})
            </button>
        </div>
      </div>

      {/* Table Container */}
      <div className="absolute w-full top-[120px] bottom-10 left-10 right-10 z-40 flex flex-col shadow-2xl rounded-2xl overflow-hidden border border-white/20 opacity-80">
        <div 
           className="flex flex-col h-full bg-cover bg-center"
           style={{ backgroundImage: `url(${Bg})` }}
        >
          <div className="flex flex-col h-full bg-white/90"> {/* Higher opacity for readability */}
            {/* Header */}
            <div className="grid grid-cols-[100px_1fr_2fr_2fr_100px] bg-gray-100 border-b py-4 text-xs font-bold text-gray-500 uppercase tracking-widest text-center">
              <div>ẢNH</div>
              <div>ISBN</div>
              <div>TÊN SÁCH</div>
              <div>THỂ LOẠI</div>
              <div>GIÁ</div>
            </div>

            {/* Body */}
            <div className="flex-1 overflow-y-auto">
              {display.map((b) => (
                <div
                  key={b.id}
                  onClick={() => setSelectedBook(b)}
                  className="grid grid-cols-[100px_1fr_2fr_2fr_100px] border-b border-gray-100 py-6 items-center hover:bg-purple-50 cursor-pointer transition"
                >
                  <div className="text-3xl text-center">ẢNH SÁCH</div>
                  <div className="font-mono text-xs text-gray-400 text-center">{b.isbn}</div>
                  <div className="font-semibold text-gray-800 px-4 text-center">{b.name}</div>
                  <div className="text-gray-500 italic text-sm text-center">{b.category.join(", ")}</div>
                  <div className="font-bold text-purple-600 text-center">${b.price}</div>
                </div>
              ))}
            </div>

            {/* Pagination */}
            <div className="p-4 flex justify-between items-center bg-gray-50 border-t">
              <div className="text-sm text-gray-500">Trang {page} / {totalPages}</div>
              <div className="flex gap-4">
                <button onClick={() => setPage(p => Math.max(p - 1, 1))} className="p-2 border rounded bg-white hover:bg-gray-100">◀</button>
                <button onClick={() => setPage(p => Math.min(p + 1, totalPages))} className="p-2 border rounded bg-white hover:bg-gray-100">▶</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Detail Modal */}
      {selectedBook && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-[60] backdrop-blur-md" onClick={() => setSelectedBook(null)}>
          <div 
           className="flex flex-col h-full bg-cover bg-center"
           style={{ backgroundImage: `url(${Bg})` }}
            onClick={(e) => e.stopPropagation()}
          >
            {/* Design overlay for modal */}
            <div className="absolute top-0 left-0 w-full h-2 bg-purple-600"></div>
            
            <button onClick={() => setSelectedBook(null)} className="absolute top-4 right-4 text-gray-400 hover:text-black">✕</button>

            <div className="flex flex-col items-center">
                <div className="text-7xl mb-6">*ẢNH SÁCH*</div>
                <h2 className="text-2xl font-black text-gray-800 mb-1">{selectedBook.name}</h2>
                <span className="text-xs font-mono bg-gray-100 px-2 py-1 rounded text-gray-500 mb-6">ISBN: {selectedBook.isbn}</span>
                
                <div className="w-full text-sm text-gray-600 space-y-3 mb-8">
                    <p><strong>Tác giả:</strong> {selectedBook.author}</p>
                    <p><strong>Thể loại:</strong> {selectedBook.category.join(", ")}</p>
                    <p><strong>Nhà xuất bản:</strong> {selectedBook.publisher}</p>
                    <p><strong>Số lượng kho:</strong> <span className="text-green-600 font-bold">{selectedBook.stock} cuốn</span></p>
                </div>

                <div className="w-full flex items-center justify-between mb-6">
                    <span className="text-3xl font-bold text-purple-600">${selectedBook.price}</span>
                    <button 
                        onClick={
                          () => {setQuantityModal(selectedBook);
                          setSelectedBook(null);
                        }}
                        className="bg-purple-600 text-white px-8 py-3 rounded-xl font-bold hover:shadow-lg transition transform hover:-translate-y-1"
                    >
                        Mua Ngay
                    </button>
                </div>
            </div>
          </div>
        </div>
      )}

      {/* Quantity Modal */}
      {quantityModal && (
        <QuantityModal
          book={quantityModal}
          onClose={() => setQuantityModal(null)}
          onConfirm={(qty) => handleAddToCart(quantityModal, qty)}
        />
      )}
    </div>
  );
}

// Separate components stay at the bottom
function QuantityModal({ book, onClose, onConfirm }) {
  const [qty, setQty] = useState(1);
  return (
    <div className="w-full fixed inset-0 bg-black/70 flex justify-center items-center z-[70] backdrop-blur-sm">
      <div 
           className="flex flex-col h-full bg-cover bg-center"
           style={{ backgroundImage: `url(${Bg1})` }}
        >
        <h2 className="text-xl font-bold mb-4">{book.name}</h2>
        <p className="text-sm text-gray-500 mb-4">Vui lòng chọn số lượng (Tối đa: {book.stock})</p>
        
        <input
          type="number" min={1} max={book.stock}
          value={qty} onChange={(e) => setQty(Math.min(book.stock, Math.max(1, Number(e.target.value))))}
          className="border-2 border-purple-100 p-3 rounded-xl text-center text-2xl font-bold outline-none focus:border-purple-500 transition mb-4"
        />

        <div className="flex justify-between font-bold text-gray-800 mb-6">
            <span>Tổng cộng:</span>
            <span className="text-purple-600 text-xl">${(qty * book.price).toFixed(2)}</span>
        </div>

        <button onClick={() => onConfirm(qty)} className="w-full bg-purple-600 text-white py-3 rounded-xl font-bold mb-2">Xác nhận thêm</button>
        <button onClick={onClose} className="w-full text-gray-400 text-sm py-2 hover:text-gray-600">Hủy bỏ</button>
      </div>
    </div>
  );
}

function CartPage({ cart, setCart, goBack }) {
    const updateQty = (index, qty) => {
        const newCart = [...cart];
        newCart[index].quantity = Math.max(1, qty);
        setCart(newCart);
    };

    const remove = (id) => setCart(cart.filter(item => item.id !== id));
    const totalAll = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

    return (
        <div className="min-h-screen bg-gray-50 p-10 flex flex-col items-center">
            <div className="w-full max-w-4xl">
                <button onClick={goBack} className="mb-6 flex items-center gap-2 text-purple-600 font-bold">
                    ← Tiếp tục xem sách
                </button>
                <h1 className="text-4xl font-black mb-10 text-gray-800">Giỏ hàng của bạn</h1>

                <div className="bg-white rounded-3xl shadow-xl overflow-hidden mb-10">
                    {cart.length === 0 ? (
                        <div className="p-20 text-center text-gray-400">Giỏ hàng đang trống...</div>
                    ) : (
                        cart.map((item, i) => (
                            <div key={item.id} className="p-6 border-b border-gray-100 flex items-center justify-between">
                                <div className="flex gap-4 items-center">
                                    <div className="text-4xl">📘</div>
                                    <div>
                                        <div className="font-bold text-gray-800">{item.name}</div>
                                        <div className="text-xs text-gray-400">${item.price} / cuốn</div>
                                    </div>
                                </div>
                                <div className="flex items-center gap-10">
                                    <input type="number" value={item.quantity} onChange={(e) => updateQty(i, Number(e.target.value))} className="w-20 p-2 border rounded-xl text-center font-bold" />
                                    <div className="w-24 font-bold text-right">${(item.price * item.quantity).toFixed(2)}</div>
                                    <button onClick={() => remove(item.id)} className="text-red-400 hover:text-red-600 font-bold">✕</button>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                <div className="flex justify-between items-center p-8 bg-purple-600 rounded-3xl text-white shadow-xl">
                    <div>
                        <div className="text-purple-200 uppercase text-xs font-bold">Tổng thanh toán</div>
                        <div className="text-4xl font-black">${totalAll.toFixed(2)}</div>
                    </div>
                    <button className="bg-white text-purple-600 px-10 py-4 rounded-2xl font-black shadow-lg hover:scale-105 transition">THANH TOÁN NGAY</button>
                </div>
            </div>
        </div>
    );
}