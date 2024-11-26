import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export const parseCapacity = (size: number) => {
  if (isNaN(size)) return null;
  try {
    if (size < 1024) return size.toFixed(2) + ' GB';
    return (size / 1024).toFixed(2) + ' TB';
  } catch (err) {
    console.error(err);
  }
};
