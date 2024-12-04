import { ThemeProvider } from '@/components/theme-provider';
import { BrowserRouter, Routes, Route } from 'react-router';
import Search from '@/screens/Search';
import Home from './screens/Home';
import NavBar from './components/NavBar';
import FrequencyCount from './screens/FrequencyCount';
import History from './screens/History';
import { Toaster } from './components/ui/toaster';

const App = () => {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <BrowserRouter>
        <NavBar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/search" element={<Search />} />
          <Route path="/frequency" element={<FrequencyCount />} />
          <Route path="/search_history" element={<History />} />
        </Routes>
        <Toaster />
      </BrowserRouter>
    </ThemeProvider>
  );
};

export default App;
