import React, { useState, useEffect } from 'react';

// Use your image imports (adjust paths if needed)
import img1 from './assets/login.png'; 
import img2 from './assets/login1.png';
import img3 from './assets/login2.png';

const images = [img1, img2, img3];

export default function App() {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [view, setView] = useState('login'); // 'login' or 'register'

  // Automatic Background Switch
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % images.length);
    }, 5000); 
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="h-screen w-full flex overflow-hidden">
      
      {/* --- LEFT SIDE: BACKGROUND SLIDESHOW --- */}
      <div className="relative flex-[7] h-full overflow-hidden bg-gray-200">
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

      {/* --- RIGHT SIDE: FORM PANEL --- */}
      <div className="relative w-full max-w-[280px] h-[500px] overflow-hidden">
<div className="relative h-[80px] mb-10 overflow-hidden">

  {/* LOGIN TITLE */}
  <h2
    className={`absolute w-full text-center text-lg uppercase tracking-widest text-[#333] transition-all duration-500 ${
      view === 'login'
        ? 'opacity-100 translate-y-0'
        : 'opacity-0 -translate-y-4'
    }`}
  >
    ỨNG DỤNG QUẢN LÝ<br />CỬA HÀNG SÁCH
  </h2>

  {/* REGISTER TITLE */}
  <h2
    className={`absolute w-full text-center text-lg uppercase tracking-widest text-[#333] transition-all duration-500 ${
      view === 'register'
        ? 'opacity-100 translate-y-0'
        : 'opacity-0 translate-y-4'
    }`}
  >
    TẠO TÀI KHOẢN
  </h2>

  {/* FORGOT TITLE */}
  <h2
    className={`absolute w-full text-center text-lg uppercase tracking-widest text-[#333] transition-all duration-500 ${
      view === 'forgot'
        ? 'opacity-100 translate-y-0'
        : 'opacity-0 translate-y-4'
    }`}
  >
    KHÔI PHỤC MẬT KHẨU
  </h2>

</div>
  {/* LOGIN */}
  
  <div
    className={`absolute w-full transition-all duration-500 ${
      view === 'login'
        ? 'opacity-100 translate-x-0'
        : 'opacity-0 -translate-x-full'
    }`}
  >
    <div className="flex flex-col gap-4">
      <input className="input-field" placeholder="Username" />
      <input className="input-field" type="password" placeholder="Password" />

      <div className="pt-2 flex flex-col gap-4">
        <button className="btn">Đăng nhập</button>

        <button onClick={() => setView('register')} className="btn">
          Đăng ký
        </button>

        <button
          onClick={() => setView('forgot')}
          className="text-xs text-[#555] hover:underline text-center"
        >
          Quên mật khẩu?
        </button>
      </div>
    </div>
  </div>

  {/* REGISTER */}
  <div
    className={`absolute w-full transition-all duration-500 ${
      view === 'register'
        ? 'opacity-100 translate-x-0'
        : 'opacity-0 translate-x-full'
    }`}
  >
    <div className="flex flex-col gap-3">
      <p className="text-sm font-semibold text-[#444] border-b border-purple-500/20 mb-2">
        TẠO TÀI KHOẢN MỚI
      </p>

      <input className="input-field" placeholder="Username" />
      <input className="input-field" type="email" placeholder="Email" />
      <input className="input-field" placeholder="Full Name" />
      <input className="input-field" placeholder="Phone Number" />
      <input className="input-field" type="password" placeholder="Password" />
      <input className="input-field" type="password" placeholder="Confirm Password" />

      <div className="pt-4 flex flex-col gap-3">
        <button className="btn">Xác nhận đăng ký</button>
        <button onClick={() => setView('login')} className="btn-outline">
          Quay lại đăng nhập
        </button>
      </div>
    </div>
  </div>

  {/* FORGOT PASSWORD */}
  <div
    className={`absolute w-full transition-all duration-500 ${
      view === 'forgot'
        ? 'opacity-100 translate-x-0'
        : 'opacity-0 translate-x-full'
    }`}
  >
    <div className="flex flex-col gap-4">
      <p className="text-sm font-semibold text-[#444] border-b border-purple-500/20 mb-2">
        KHÔI PHỤC MẬT KHẨU
      </p>

      <input className="input-field" type="email" placeholder="Nhập email của bạn" />

      <button className="btn">Gửi yêu cầu</button>

      <button
        onClick={() => setView('login')}
        className="text-xs text-[#555] hover:underline text-center"
      >
        ← Quay lại đăng nhập
      </button>
    </div>
  </div>

</div>
      
      {/* Local Styles for cleaner code */}
      <style>{`
        .input-field {
          width: 100%;
          border: 1px solid rgba(139, 92, 246, 0.4);
          background-color: rgba(255, 255, 255, 0.25);
          padding: 10px;
          font-size: 0.875rem;
          outline: none;
          color: #333;
        }

        .btn {
          width: 100%;
          border: 1px solid rgba(139, 92, 246, 0.5);
          background: rgba(255,255,255,0.2);
          padding: 10px;
          font-size: 0.875rem;
          color: #444;
          transition: 0.3s;
        }

        .btn:hover {
          background: rgba(255,255,255,0.4);
        }

        .btn-outline {
          width: 100%;
          border: 1px solid #333;
          padding: 10px;
          font-size: 0.875rem;
          color: #333;
        }
      `}</style>

    </div>
  );
}