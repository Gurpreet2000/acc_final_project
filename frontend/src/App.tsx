import React, { useEffect, useState } from 'react';
import SearchBar from './components/SearchBar';
import apiCall from '@/lib/apiCall';
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from './components/ui/table';
import PlanList from './components/PlanList';
import { ThemeProvider } from './components/theme-provider';

const App = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [list, setList] = useState([]);

  useEffect(() => {
    if (!searchTerm) {
      setSuggestions([]);
      return;
    }

    apiCall
      .get('/auto_complete', { params: { q: searchTerm } })
      .then(res => {
        // setShowSuggestions(true);
        setSuggestions(res?.data?.data);
      })
      .catch(err => console.error(err));
  }, [searchTerm]);

  const onSearch = () => {
    apiCall
      .get('/search', { params: { q: searchTerm } })
      .then(res => setList(res?.data?.data))
      .catch(err => console.error(err))
      .finally(() => {
        setSuggestions([]);
        setShowSuggestions(false);
      });
  };

  return (
    <div className="p-4 flex h-full flex-col">
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
  );
};

export default () => (
  <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
    <App />
  </ThemeProvider>
);
