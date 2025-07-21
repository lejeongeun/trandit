import React from 'react';
import '../styles/CommonButton.css';

export default function CommonButton({
  children,
  type = 'button',
  variant = 'primary', // 'primary', 'secondary', 'danger' 등 확장 가능
  size = 'md',         // 'sm', 'md', 'lg' 등 확장 가능
  ...props
}) {
  return (
    <button
      type={type}
      className={`common-btn ${variant} ${size}`}
      {...props}
    >
      {children}
    </button>
  );
} 