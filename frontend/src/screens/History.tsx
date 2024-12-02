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

const History = () => {
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    apiCall
      .get('/search_history')
      .then(res => {
        setData(res?.data?.data);
        res.data?.data?.forEach((e: { [key: string]: number }) => {
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
          <TableHead>Word</TableHead>

          <TableHead className="text-right">Frequency</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        {data?.map((e: { [key: string]: number }) => (
          <TableRow>
            <TableCell>{Object.keys(e)[0]}</TableCell>
            <TableCell className="text-right">{Object.values(e)[0]}</TableCell>
          </TableRow>
        ))}
      </TableBody>

      <TableFooter>
        <TableRow>
          <TableCell colSpan={3}>Total</TableCell>
          <TableCell className="text-right">{total}</TableCell>
        </TableRow>
      </TableFooter>
    </Table>
  );
};

export default History;
