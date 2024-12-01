import { ThemeProvider } from '@/components/theme-provider';
import { BrowserRouter, Routes, Route } from 'react-router';
import Search from '@/screens/Search';
import Home from './screens/Home';
import NavBar from './components/NavBar';
import History from './screens/History';

const App = () => {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <BrowserRouter>
        <NavBar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/search" element={<Search />} />
          <Route path="/history" element={<History />} />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
};

export default App;
