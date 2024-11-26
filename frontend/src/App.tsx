import { useEffect, useState } from 'react';
import SearchBar from './components/SearchBar';
import apiCall from '@/lib/apiCall';
import PlanList from './components/PlanList';
import { ThemeProvider } from './components/theme-provider';
import StorageSelector from './components/StorageSelector';
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from './components/ui/resizable';
import { Separator } from './components/ui/separator';
import { Button } from './components/ui/button';

const App = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [list, setList] = useState([]);
  const [capacity, setCapacity] = useState({ min: 1024, max: 1024 * 75 });

  useEffect(() => {
    if (!searchTerm) {
      setSuggestions([]);
      return;
    }

    apiCall
      .get('/auto_complete', {
        params: { q: searchTerm },
      })
      .then(res => {
        // setShowSuggestions(true);
        setSuggestions(res?.data?.data);
      })
      .catch(err => console.error(err));
  }, [searchTerm]);

  const onSearch = () => {
    apiCall
      .get('/search', {
        params: {
          q: searchTerm,
          minStorage: capacity.min,
          maxStorage: capacity.max,
        },
      })
      .then(res => setList(res?.data?.data))
      .catch(err => console.error(err))
      .finally(() => {
        setSuggestions([]);
        setShowSuggestions(false);
      });
  };

  return (
    <div className="p-4 flex h-full flex-row gap-5">
      <div className="flex flex-col gap-3 p-5">
        <StorageSelector setValue={setCapacity} value={capacity} />
        <Button onClick={onSearch}>Filter</Button>
      </div>
      <Separator orientation="vertical" />
      <div className="flex flex-col flex-1">
        <SearchBar
          data={suggestions}
          value={searchTerm}
          setValue={setSearchTerm}
          onSearch={onSearch}
          showSuggestions={showSuggestions}
          setShowSuggestions={setShowSuggestions}
        />
        <PlanList data={list} />
      </div>
    </div>
  );
};

export default () => (
  <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
    <App />
  </ThemeProvider>
);
