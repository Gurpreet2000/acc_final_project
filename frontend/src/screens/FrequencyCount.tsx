import { useEffect, useState } from 'react';
import apiCall from '@/lib/apiCall';
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

const FrequencyCount = () => {
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    apiCall
      .get('/frequency_counter')
      .then(res => {
        const list = Array.isArray(res.data?.list) ? res.data?.list : [];
        setData(list);
        list?.forEach((e: { [key: string]: number }) => {
          setTotal(p => p + (Object.values(e)?.[0] || 0));
        });
      })
      .catch(err => {
        console.error(err);
      });
  }, []);
  return (
    <Table>
      <TableCaption>A list of your searches.</TableCaption>
      <TableHeader>
        <TableRow>
          <TableHead className="px-5">Word</TableHead>

          <TableHead className="px-5 text-right">Frequency</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        {data?.map((e: { [key: string]: number }) => (
          <TableRow>
            <TableCell className="px-5">{Object.keys(e)[0]}</TableCell>
            <TableCell className="px-5 text-right">
              {Object.values(e)[0]}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>

      <TableFooter className="fixed bottom-0 left-0 right-0 z-50">
        <TableRow className="w-[100%] flex flex-row">
          <TableCell className="px-5 flex-1">Total</TableCell>
          <TableCell className="px-5 text-right">{total}</TableCell>
        </TableRow>
      </TableFooter>
    </Table>
  );
};

export default FrequencyCount;
